package tech.aiflowy.ai.service.impl;


import com.agentsflex.rerank.DefaultRerankModel;
import org.springframework.beans.factory.annotation.Autowired;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiKnowledgeMapper;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.SearchWrapper;
import com.agentsflex.core.store.StoreOptions;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static tech.aiflowy.ai.config.RagRerankModelUtil.getRerankModel;

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


    @Override
    public Result search(BigInteger id, String keyword) {
        AiKnowledge knowledge = getById(id);
        if (knowledge == null) {
            return Result.fail(1, "知识库不存在");
        }

        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            return Result.fail(2, "知识库没有配置向量库");
        }

        AiLlm aiLlm = llmService.getById(knowledge.getVectorEmbedLlmId());
        if (aiLlm == null) {
            return Result.fail(3, "知识库没有配置向量模型");
        }

        documentStore.setEmbeddingModel(aiLlm.toLlm());

        SearchWrapper wrapper = new SearchWrapper();
        wrapper.setMaxResults(5);
        wrapper.setText(keyword);

        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        options.setIndexName(knowledge.getVectorStoreCollection());

        // 并行查询：向量库 + 搜索引擎
        CompletableFuture<List<Document>> vectorFuture = CompletableFuture.supplyAsync(() ->
                documentStore.search(wrapper, options)
        );

        CompletableFuture<List<Document>> searcherFuture = CompletableFuture.supplyAsync(() -> {
            if (knowledge.getSearchEngineEnable() == null || !knowledge.getSearchEngineEnable() || searcherFactory.getSearcher() == null || searcherFactory.getSearcher().searchDocuments(keyword) == null) {
                return Collections.emptyList(); // 如果未启用搜索引擎，返回空列表
            }
            return searcherFactory.getSearcher().searchDocuments(keyword);
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
            List<Document> needRerankDocuments = new ArrayList<>(uniqueDocs.values());

            if (needRerankDocuments.isEmpty()) {
                return Result.success();
            }

            if (!knowledge.getSearchEngineEnable()) {
                return Result.success(needRerankDocuments);
            }

            AiLlm aiLlmRerank = llmService.getById(knowledge.getRerankLlmId());
            if (aiLlmRerank == null) {
                return Result.success(needRerankDocuments);
            }

            DefaultRerankModel rerankModel = getRerankModel(aiLlmRerank);
            if (rerankModel == null) {
                return Result.fail(4, "重排模型配置失败");
            }

            needRerankDocuments.forEach(item -> item.setScore(null));
            List<Document> rerank = rerankModel.rerank(keyword, needRerankDocuments);
            List<Document> filteredList = rerank.stream()
                    .filter(doc -> doc.getScore() >= 0.001)
                    .collect(Collectors.toList());
            return Result.success(filteredList);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return Result.fail(5, "查询过程中发生异常: " + e.getMessage());
        }
    }


}
