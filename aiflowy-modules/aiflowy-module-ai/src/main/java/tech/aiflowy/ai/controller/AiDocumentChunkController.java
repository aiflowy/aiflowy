package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiDocumentChunk;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.store.StoreResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *  控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiDocumentChunk")
public class AiDocumentChunkController extends BaseCurdController<AiDocumentChunkService, AiDocumentChunk> {

     @Resource
     AiKnowledgeService aiKnowledgeService;

    @Resource
    AiLlmService aiLlmService;

    @Resource
    AiDocumentChunkService aiDocumentChunkService;

    public AiDocumentChunkController(AiDocumentChunkService service) {
        super(service);
    }

    @PostMapping("update")
    public Result update(@JsonBody AiDocumentChunk aiDocumentChunk) {
        boolean success = service.updateById(aiDocumentChunk);
        if (success){
            AiDocumentChunk aiDocumentChunk1 = aiDocumentChunkService.getById(aiDocumentChunk.getId());
            AiKnowledge knowledge = aiKnowledgeService.getById(aiDocumentChunk1.getKnowledgeId());
            if (knowledge == null) {
                return Result.fail(1, "知识库不存在");
            }
            DocumentStore documentStore = knowledge.toDocumentStore();
            if (documentStore == null) {
                return Result.fail(2, "知识库没有配置向量库");
            }
            // 设置向量模型
            AiLlm aiLlm = aiLlmService.getById(knowledge.getVectorEmbedLlmId());
            if (aiLlm == null) {
                return Result.fail(3, "知识库没有配置向量模型");
            }
            Llm embeddingModel = aiLlm.toLlm();
            documentStore.setEmbeddingModel(embeddingModel);
            StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
            Document document = Document.of(aiDocumentChunk.getContent());
            document.setId(aiDocumentChunk.getId());
            StoreResult result = documentStore.update(document, options); // 更新已有记录
            return Result.success(result);
        }
        return Result.create(false);
    }

    @PostMapping("removeChunk")
    public Result remove(@JsonBody(value = "id", required = true) BigInteger chunkId) {
        AiDocumentChunk docChunk =  aiDocumentChunkService.getById(chunkId);
        if (docChunk == null) {
            return Result.fail(1, "记录不存在");
        }
        AiKnowledge knowledge = aiKnowledgeService.getById(docChunk.getKnowledgeId());
        if (knowledge == null) {
            return Result.fail(2, "知识库不存在");
        }
        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            return Result.fail(3, "知识库没有配置向量库");
        }
        // 设置向量模型
        AiLlm aiLlm = aiLlmService.getById(knowledge.getVectorEmbedLlmId());
        if (aiLlm == null) {
            return Result.fail(4, "知识库没有配置向量模型");
        }
        Llm embeddingModel = aiLlm.toLlm();
        documentStore.setEmbeddingModel(embeddingModel);
        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        ArrayList deleteList = new ArrayList<>();
        deleteList.add(chunkId);
        StoreResult deleteResult = documentStore.delete(deleteList, options);
        return super.remove(chunkId);
    }
}