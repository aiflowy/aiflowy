package tech.aiflowy.ai.service.impl;


import com.agentsflex.core.document.Document;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.mapper.AiDocumentChunkMapper;
import tech.aiflowy.ai.mapper.AiKnowledgeMapper;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.ai.utils.CustomBeanUtils;
import tech.aiflowy.ai.utils.RegexUtils;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * 服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiKnowledgeServiceImpl extends ServiceImpl<AiKnowledgeMapper, AiKnowledge> implements AiKnowledgeService {

    @Resource
    private AiLlmService llmService;

    @Resource
    private AiDocumentChunkService chunkService;

    @Autowired
    private SearcherFactory searcherFactory;

    @Autowired
    private AiDocumentChunkMapper aiDocumentChunkMapper;


    @Override
    public List<Document> search(BigInteger id, String keyword) {
//        AiKnowledge knowledge = getById(id);
//        if (knowledge == null) {
//            throw new BusinessException("知识库不存在");
//        }
//
//        DocumentStore documentStore = knowledge.toDocumentStore();
//        if (documentStore == null) {
//            throw new BusinessException("知识库没有配置向量库");
//        }
//
//        AiLlm aiLlm = llmService.getById(knowledge.getVectorEmbedLlmId());
//        if (aiLlm == null) {
//            throw new BusinessException("知识库没有配置向量模型");
//        }
//
//        documentStore.setEmbeddingModel(aiLlm.toLlm());
//
//        SearchWrapper wrapper = new SearchWrapper();
//        wrapper.setMaxResults(5);
//        wrapper.setText(keyword);
//
//        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
//        options.setIndexName(knowledge.getVectorStoreCollection());
//
//        // 并行查询：向量库 + 搜索引擎
//        CompletableFuture<List<Document>> vectorFuture = CompletableFuture.supplyAsync(() ->
//                documentStore.search(wrapper, options)
//        );
//
//        CompletableFuture<List<Document>> searcherFuture = CompletableFuture.supplyAsync(() -> {
//            DocumentSearcher searcher = searcherFactory.getSearcher();
//            if (searcher == null || !knowledge.isSearchEngineEnabled()) {
//                return Collections.emptyList();
//            }
//            List<Document> documents = searcher.searchDocuments(keyword);
//            return documents == null ? Collections.emptyList() : documents;
//        });
//
//        // 合并两个查询结果
//        CompletableFuture<Map<String, Document>> combinedFuture = vectorFuture.thenCombine(
//                searcherFuture,
//                (vectorDocs, searcherDocs) -> {
//                    Map<String, Document> uniqueDocs = new HashMap<>();
//                    vectorDocs.forEach(doc -> uniqueDocs.putIfAbsent(doc.getId().toString(), doc));
//                    searcherDocs.forEach(doc -> uniqueDocs.putIfAbsent(doc.getId().toString(), doc));
//                    return uniqueDocs;
//                }
//        );
//
//        try {
//            Map<String, Document> uniqueDocs = combinedFuture.get(); // 阻塞等待所有查询完成
//            List<Document> needRerankDocuments = new ArrayList<>(uniqueDocs.values());
//            needRerankDocuments.sort((doc1, doc2) -> Double.compare(doc2.getScore(), doc1.getScore()));
//            needRerankDocuments.forEach(item ->{
//                AiDocumentChunk aiDocumentChunk = aiDocumentChunkMapper.selectOneById((Serializable) item.getId());
//                if (aiDocumentChunk != null && !StringUtil.noText(aiDocumentChunk.getContent())){
//                    item.setContent(aiDocumentChunk.getContent());
//                }
//
//            });
//            if (needRerankDocuments.isEmpty()) {
//
//                return Collections.emptyList();
//            }
//
//            if (!knowledge.getSearchEngineEnable()) {
//                return needRerankDocuments;
//            }
//
//            AiLlm aiLlmRerank = llmService.getById(knowledge.getRerankLlmId());
//            if (aiLlmRerank == null) {
//                return needRerankDocuments;
//            }
//
//            DefaultRerankModel rerankModel = getRerankModel(aiLlmRerank);
//            if (rerankModel == null) {
//                throw new BusinessException("重排模型配置失败");
//            }
//
//            needRerankDocuments.forEach(item -> item.setScore(null));
//            List<Document> rerank = rerankModel.rerank(keyword, needRerankDocuments);
//            List<Document> filteredList = rerank.stream()
//                    .filter(doc -> doc.getScore() >= 0.001)
//                    .collect(Collectors.toList());
//            return filteredList;
//        } catch (InterruptedException | ExecutionException e) {
//            Thread.currentThread().interrupt();
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
        return Collections.emptyList();
    }

    @Override
    public AiKnowledge getDetail(String idOrAlias) {

        AiKnowledge knowledge = null;

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
    public AiKnowledge getByAlias(String idOrAlias) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("alias", idOrAlias);

        return getOne(queryWrapper);

    }


    @Override
    public boolean updateById(AiKnowledge entity) {
        AiKnowledge aiKnowledge = getById(entity.getId());
        if (aiKnowledge == null) {
            throw new BusinessException("bot 不存在");
        }

        CustomBeanUtils.copyPropertiesIgnoreNull(entity, aiKnowledge);

        if ("".equals(aiKnowledge.getAlias())) {
            aiKnowledge.setAlias(null);
        }


        return super.updateById(aiKnowledge, false);
    }

}
