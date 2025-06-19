package tech.aiflowy.ai.service.impl;


import com.agentsflex.rerank.DefaultRerankModel;
import com.agentsflex.search.engine.service.DocumentSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import tech.aiflowy.ai.config.AiEsConfig;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.AiDocumentChunk;
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
        wrapper.setMaxResults(Integer.valueOf(5));
        wrapper.setText(keyword);

        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        options.setIndexName(knowledge.getVectorStoreCollection());
        // 检索向量知识库返回的结果
        List<Document> vectorDocuments = documentStore.search(wrapper, options);

        if (vectorDocuments == null || vectorDocuments.isEmpty()) {
            return Result.success();
        }

        // 判断是否配置了搜索引擎相关配置，如果该知识库没有配置搜索引擎则不进行重排,直接返回向量化的数据结果
        if (!knowledge.getSearchEngineEnable()){
            return Result.success(vectorDocuments);
        }

        AiLlm aiLlmRerank = llmService.getById(knowledge.getRerankLlmId());
        if (aiLlmRerank == null){
            return Result.success(vectorDocuments);
        }
        // 配置重排模型
        DefaultRerankModel rerankModel = getRerankModel(aiLlmRerank);
        if (rerankModel == null){
            return Result.fail(4, "重排模型配置失败");
        }

        // 通过搜索引擎检索
        // 配置搜索引擎
        if (searcherFactory.getSearcher() == null){
            return Result.success(vectorDocuments);
        }
        DocumentSearcher searcher = searcherFactory.getSearcher();
        // 搜索引擎返回的结果
        List<Document> searcherDocuments = searcher.searchDocuments(keyword);
        // 合并两个List，并按id去重（保留第一个出现的Document）
        // 使用LinkedHashMap保持插入顺序
        Map<String, Document> uniqueDocs = new LinkedHashMap<>();
        vectorDocuments.forEach(doc -> uniqueDocs.putIfAbsent(doc.getId().toString(), doc));
        searcherDocuments.forEach(doc -> uniqueDocs.putIfAbsent(doc.getId().toString(), doc));

        List<Document> needRerankDocuments = new ArrayList<>(uniqueDocs.values());
        needRerankDocuments.forEach(item ->item.setScore(null));
        List<Document> rerank = rerankModel.rerank(keyword, needRerankDocuments);
        List<Document> filteredList = rerank.stream()
                .filter(doc -> doc.getScore() >= 0.001)  // 保留score≥0.01的文档
                .collect(Collectors.toList());
        return Result.success(filteredList);
    }


}
