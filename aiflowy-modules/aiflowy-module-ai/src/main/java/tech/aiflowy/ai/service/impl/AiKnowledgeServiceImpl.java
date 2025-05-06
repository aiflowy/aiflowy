package tech.aiflowy.ai.service.impl;

import com.agentsflex.store.elasticsearch.ElasticSearchVectorStore;
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
import com.agentsflex.core.util.Maps;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
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
        List<Document> results = documentStore.search(wrapper, options);

        if (results == null || results.isEmpty()) {
            return Result.success();
        }

        List<AiDocumentChunk> chunks = new ArrayList<>();

        for (Document result : results) {
            Object resultId = result.getId();
            Double similarityScore;
            if (documentStore instanceof ElasticSearchVectorStore){
                similarityScore = (Double) result.getMetadataMap().get("_score");
            } else {
                similarityScore = result.getScore();
            }

            // 使用 BigDecimal 保留小数点后四位
            BigDecimal formatScore = new BigDecimal(similarityScore);
            formatScore = formatScore.setScale(4, RoundingMode.HALF_UP); // 四舍五入保留四位小数
            similarityScore = formatScore.doubleValue();
            AiDocumentChunk documentChunk = chunkService.getMapper().selectOneWithRelationsByMap(
                    Maps.of("id", resultId));
            if (documentChunk == null){
                continue;
            }
            documentChunk.setSimilarityScore(similarityScore);
            chunks.add(documentChunk);
        }

        return Result.success(chunks);
    }
}
