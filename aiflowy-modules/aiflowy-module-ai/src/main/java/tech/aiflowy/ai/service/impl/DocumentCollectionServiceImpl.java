package tech.aiflowy.ai.service.impl;


import com.agentsflex.core.document.Document;
import com.agentsflex.core.model.rerank.RerankModel;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.search.engine.service.DocumentSearcher;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.DocumentChunk;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.ai.mapper.DocumentChunkMapper;
import tech.aiflowy.ai.mapper.DocumentCollectionMapper;
import tech.aiflowy.ai.service.DocumentChunkService;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.ai.utils.CustomBeanUtils;
import tech.aiflowy.ai.utils.RegexUtils;
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

    @Resource
    private DocumentChunkService chunkService;

    @Autowired
    private SearcherFactory searcherFactory;

    @Autowired
    private DocumentChunkMapper documentChunkMapper;


    @Override
    public List<Document> search(BigInteger id, String keyword) {
        DocumentCollection documentCollection = getById(id);
        if (documentCollection == null) {
            throw new BusinessException("知识库不存在");
        }

        DocumentStore documentStore = documentCollection.toDocumentStore();
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
        // 最低相似度
        float minSimilarity = (float) documentCollection.getOptionsByKey(KEY_SIMILARITY_THRESHOLD);
        SearchWrapper wrapper = new SearchWrapper();
        wrapper.setMaxResults(docRecallMaxNum);
        wrapper.setMinScore((double) minSimilarity);
        wrapper.setText(keyword);
        wrapper.eq(KEY_DOCUMENT_ID, documentCollection.getId());
        StoreOptions options = StoreOptions.ofCollectionName(documentCollection.getVectorStoreCollection());
        options.setIndexName(documentCollection.getVectorStoreCollection());

        // 并行查询：向量库 + 搜索引擎
        CompletableFuture<List<Document>> vectorFuture = CompletableFuture.supplyAsync(() ->
                documentStore.search(wrapper, options)
        );

        CompletableFuture<List<Document>> searcherFuture = CompletableFuture.supplyAsync(() -> {
            DocumentSearcher searcher = searcherFactory.getSearcher((String) documentCollection.getOptionsByKey(KEY_SEARCH_ENGINE_TYPE));
            if (searcher == null || !documentCollection.isSearchEngineEnabled()) {
                return Collections.emptyList();
            }
            List<Document> documents = searcher.searchDocuments(keyword);
            return documents == null ? Collections.emptyList() : documents;
        });

        // 合并两个查询结果
        CompletableFuture<Map<String, Document>> combinedFuture = vectorFuture.thenCombine(
                searcherFuture,
                (vectorDocs, searcherDocs) -> {
                    Map<String, Document> uniqueDocs = new HashMap<>();
                    vectorDocs.forEach(doc -> uniqueDocs.putIfAbsent(doc.getId().toString(), doc));
                    searcherDocs.forEach(doc -> uniqueDocs.putIfAbsent(doc.getId().toString(), doc));
                    return uniqueDocs;
                }
        );

        try {
            Map<String, Document> uniqueDocs = combinedFuture.get(); // 阻塞等待所有查询完成
            List<Document> searchDocuments = new ArrayList<>(uniqueDocs.values());
            searchDocuments.sort((doc1, doc2) -> Double.compare(doc2.getScore(), doc1.getScore()));
            searchDocuments.forEach(item ->{
                DocumentChunk documentChunk = documentChunkMapper.selectOneById((Serializable) item.getId());
                if (documentChunk != null && !StringUtil.noText(documentChunk.getContent())){
                    item.setContent(documentChunk.getContent());
                }

            });
            if (searchDocuments.isEmpty()) {
                return Collections.emptyList();
            }
            if (documentCollection.getRerankModelId() == null) {
                return formatDocuments(searchDocuments, minSimilarity, docRecallMaxNum);
            }

            Model modelRerank = llmService.getModelInstance(documentCollection.getRerankModelId());

            RerankModel rerankModel = modelRerank.toRerankModel();
            if (rerankModel == null) {
                return formatDocuments(searchDocuments, minSimilarity, docRecallMaxNum);
            }

            searchDocuments.forEach(item -> item.setScore(null));
            List<Document> rerankDocuments = rerankModel.rerank(keyword, searchDocuments);
            return formatDocuments(rerankDocuments, minSimilarity, docRecallMaxNum);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
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
     * 格式化文档列表
     *
     * @param documents 文档列表
     * @param minSimilarity 最小相似度
     * @return 格式化后的文档列表
     */
    public List<Document> formatDocuments(List<Document> documents, float minSimilarity, int maxResults) {
        return documents.stream()
                // 过滤掉分数为空 或 分数低于最小值的文档
                .filter(document -> {
                    Float score = document.getScore();
                    return score != null && score >= minSimilarity;
                })
                // 格式化保留四位小数
                .map(document -> {
                    Float score = document.getScore();
                    BigDecimal bd = new BigDecimal(score.toString());
                    bd = bd.setScale(4, RoundingMode.HALF_UP);
                    Float roundedScore = bd.floatValue();
                    document.setScore(roundedScore);
                    return document;
                })
                // 按score降序排序（分数最高的排前面）
                .sorted(Comparator.comparing(Document::getScore, Comparator.reverseOrder()))
                // 限制只保留前maxResults条
                .limit(maxResults)
                .collect(Collectors.toList());
    }
}
