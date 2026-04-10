package tech.aiflowy.ai.service.impl;


import com.agentsflex.core.document.Document;
import com.agentsflex.core.model.rerank.RerankModel;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.util.CollectionUtil;
import com.agentsflex.search.engine.service.DocumentSearcher;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.DocumentChunk;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.ai.entity.VectorDatabase;
import tech.aiflowy.ai.mapper.DocumentChunkMapper;
import tech.aiflowy.ai.mapper.DocumentCollectionMapper;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.ai.service.VectorDatabaseService;
import tech.aiflowy.ai.utils.CustomBeanUtils;
import tech.aiflowy.ai.utils.DocumentScoreCalculator;
import tech.aiflowy.ai.utils.RegexUtils;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static tech.aiflowy.ai.entity.DocumentCollection.*;
import static tech.aiflowy.ai.entity.DocumentCollection.KEY_SEARCHER_WEIGHT;
import static tech.aiflowy.ai.entity.DocumentCollection.KEY_VECTOR_WEIGHT;

/**
 * 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class DocumentCollectionServiceImpl extends ServiceImpl<DocumentCollectionMapper, DocumentCollection> implements DocumentCollectionService {

    @Resource
    private ModelService llmService;
    @Autowired
    private SearcherFactory searcherFactory;
    @Autowired
    private DocumentChunkMapper documentChunkMapper;
    @Resource
    private VectorDatabaseService vectorDatabaseService;

    private static final Integer MAX_RECALL_DOC_NUM = 10;

    @Override
    public List<Document> search(BigInteger id, String keyword) {
        DocumentCollection documentCollection = getById(id);
        if (documentCollection == null) {
            throw new BusinessException("知识库不存在");
        }
        BigInteger vectorDatabaseId = documentCollection.getVectorDatabaseId();
        VectorDatabase vectorDatabase = vectorDatabaseService.getById(vectorDatabaseId);
        if (vectorDatabase == null) {
            throw new BusinessException("向量数据库没有配置");
        }
        DocumentStore documentStore = vectorDatabase.toDocumentStore(documentCollection.getVectorOtherConfig());
        if (documentStore == null) {
            throw new BusinessException("知识库没有配置向量库");
        }

        Model model = llmService.getModelInstance(documentCollection.getVectorEmbedModelId());
        if (model == null) {
            throw new BusinessException("知识库没有配置向量模型");
        }

        documentStore.setEmbeddingModel(model.toEmbeddingModel());
        // 最大召回知识条数
        Integer docRecallMaxNum = (Integer) documentCollection.getOptionsByKey(KEY_DOC_RECALL_MAX_NUM);
        // 混合最低相似度最小值
        float minMixedSimilarity = (float) documentCollection.getOptionsByKey(KEY_MIXED_SIMILARITY_THRESHOLD);
        SearchWrapper wrapper = new SearchWrapper();
        wrapper.setMaxResults(docRecallMaxNum);
        wrapper.setMinScore((double) minMixedSimilarity);
        wrapper.setText(keyword);
        // 过滤知识库
        Map<String, Object> metadataFilters = new HashMap<>();
        metadataFilters.put(KEY_DOCUMENT_ID, String.valueOf(documentCollection.getId()));
        StoreOptions options = StoreOptions.ofCollectionName(documentCollection.getVectorStoreCollection());
        options.setIndexName(documentCollection.getVectorStoreCollection());

        // 并行查询：向量库 + 搜索引擎
        CompletableFuture<List<Document>> vectorFuture = CompletableFuture.supplyAsync(() ->
                documentStore.search(wrapper, options)
        );

        CompletableFuture<List<Document>> searcherFuture = CompletableFuture.supplyAsync(() -> {
            DocumentSearcher searcher = searcherFactory.getSearcher((String) documentCollection.getOptionsByKey(KEY_SEARCH_ENGINE_TYPE));
            if (searcher == null) {
                return Collections.emptyList();
            }
            List<Document> documents = searcher.searchDocuments(keyword, MAX_RECALL_DOC_NUM, metadataFilters);
            return documents == null ? Collections.emptyList() : documents;
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(vectorFuture, searcherFuture);
        try {
            combinedFuture.get();
            List<Document> vectorDocuments = vectorFuture.get();
            List<Document> searcherDocuments = searcherFuture.get();
            Double vectorWeight = (Double) documentCollection.getOptionsByKey(KEY_VECTOR_WEIGHT);
            Double searcherWeight = (Double) documentCollection.getOptionsByKey(KEY_SEARCHER_WEIGHT);
            List<Document> documents = DocumentScoreCalculator.calculateTotalScore(vectorDocuments, searcherDocuments, vectorWeight, searcherWeight);
            List<BigInteger> chunkIds = documents.stream().map(item -> new BigInteger(String.valueOf(item.getId()))).toList();
            if (!CollectionUtil.hasItems(chunkIds)) {
                return Collections.emptyList();
            }
            List<DocumentChunk> documentChunks = documentChunkMapper.selectListByIds(chunkIds);
            documents.forEach(item -> {
                if (documentChunks != null) {
                    documentChunks.forEach(documentChunk -> {
                        if (new BigInteger((String.valueOf(item.getId())) ).equals(documentChunk.getId())) {
                            item.setContent(documentChunk.getContent());
                        }
                    });
                }
            });
            if (documents.isEmpty()) {
                return Collections.emptyList();
            }
            if (documentCollection.getRerankModelId() == null) {
                return processDocuments(documents, minMixedSimilarity, docRecallMaxNum);
            }

            Model modelRerank = llmService.getModelInstance(documentCollection.getRerankModelId());

            RerankModel rerankModel = modelRerank.toRerankModel();
            if (rerankModel == null) {
                return processDocuments(documents, minMixedSimilarity, docRecallMaxNum);
            }

            documents.forEach(item -> item.setScore(null));
            List<Document> rerankDocs = rerankModel.rerank(keyword, documents);

            // 对score扩大100倍并保留两位小数，同时过滤掉低于最小相似度的文档
            List<Document> filteredRerankDocs = rerankDocs.stream()
                    .filter(document -> {
                        Float score = document.getScore();
                        if (score != null) {
                            // 扩大100倍
                            double scaledScore = score * 100;
                            // 保留两位小数
                            BigDecimal bd = new BigDecimal(scaledScore);
                            bd = bd.setScale(2, RoundingMode.HALF_UP);
                            float finalScore = bd.floatValue();
                            document.setScore(finalScore);
                            // 过滤掉低于最小相似度的文档
                            return finalScore >= minMixedSimilarity*100;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return processDocuments(filteredRerankDocs, minMixedSimilarity, docRecallMaxNum);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DocumentCollection getDetail(String idOrAlias) {

        DocumentCollection knowledge = null;

        if (idOrAlias.matches(RegexUtils.ALL_NUMBER)) {
            knowledge = getById(idOrAlias);
            if (knowledge == null) {
                knowledge = getByAlias(idOrAlias);
            }
        }

        if (knowledge == null) {
            knowledge = getByAlias(idOrAlias);
        }

        return knowledge;
    }

    @Override
    public DocumentCollection getByAlias(String idOrAlias) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(DocumentCollection::getAlias, idOrAlias);

        return getOne(queryWrapper);

    }


    @Override
    public boolean updateById(DocumentCollection entity) {
        DocumentCollection documentCollection = getById(entity.getId());
        if (documentCollection == null) {
            throw new BusinessException("bot 不存在");
        }

        CustomBeanUtils.copyPropertiesIgnoreNull(entity, documentCollection);

        if ("".equals(documentCollection.getAlias())) {
            documentCollection.setAlias(null);
        }


        return super.updateById(documentCollection, false);
    }

    /**
     * 处理文档列表：过滤低分数记录并截取指定数量
     * @param documents 文档列表
     * @param minSimilarity 最小相似度阈值
     * @param maxResults 最大结果数
     * @return 处理后的文档列表
     */
    public List<Document> processDocuments(List<Document> documents, float minSimilarity, int maxResults) {
        return documents.stream()
                // 1. 先过滤掉分数为空 或 分数低于最小值的文档
                .filter(document -> {
                    Float score = document.getScore();
                    return score != null && score >= minSimilarity;
                })
                // 2. 按score降序排序（分数最高的排前面）
                .sorted(Comparator.comparing(Document::getScore, Comparator.reverseOrder()))
                // 3. 限制只保留前maxResults条
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    @Override
    public Result<?> beforeRemove(Collection<Serializable> ids) {
//        if (ids == null || ids.isEmpty()) {
//            throw new BusinessException("知识库id 不能为空");
//        }
//        for (Serializable id : ids) {
//            DocumentCollection documentCollection = getById(id);
//            if (documentCollection == null) {
//                return Result.fail(2, "知识库不存在");
//            }
//            QueryWrapper queryWrapper = QueryWrapper.create().eq(DocumentChunk::getDocumentCollectionId, id);
//            List<DocumentChunk> documentChunks = documentChunkMapper.selectListByQuery(queryWrapper);
//            List<BigInteger> chunkIds = new ArrayList<>();
//            if (documentChunks != null) {
//                documentChunks.forEach(item -> {
//                    chunkIds.add(item.getId());
//                });
//            }
//            DocumentStore documentStore = documentCollection.toDocumentStore();
//            StoreOptions options = StoreOptions.ofCollectionName(documentCollection.getVectorStoreCollection());
//
//            if (documentStore != null) {
//                // 删除向量数据库中的数据
//                documentStore.delete(chunkIds, options);
//            }
//
//            // 删除数据库中的文档块数据
//            documentChunkMapper.deleteBatchByIds(chunkIds);
//            QueryWrapper documentQueryWrapper = QueryWrapper.create().eq(tech.aiflowy.ai.entity.Document::getCollectionId, id);
//            // 删除数据库中的文档数据
//            documentMapper.deleteByQuery(documentQueryWrapper);
//        }
        return null;
    }
}
