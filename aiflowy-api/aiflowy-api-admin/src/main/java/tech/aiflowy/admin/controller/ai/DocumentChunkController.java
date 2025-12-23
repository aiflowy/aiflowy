package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.agentsflex.core.model.embedding.EmbeddingModel;
import tech.aiflowy.ai.entity.DocumentChunk;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.ai.service.DocumentChunkService;
import tech.aiflowy.ai.service.DocumentCollectionService;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.store.DocumentStore;
import com.agentsflex.core.store.StoreOptions;
import com.agentsflex.core.store.StoreResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/documentChunk")
@UsePermission(moduleName = "/api/v1/documentCollection")
public class DocumentChunkController extends BaseCurdController<DocumentChunkService, DocumentChunk> {

    @Resource
    DocumentCollectionService documentCollectionService;

    @Resource
    ModelService modelService;

    @Resource
    DocumentChunkService documentChunkService;

    public DocumentChunkController(DocumentChunkService service) {
        super(service);
    }

    @PostMapping("update")
    @SaCheckPermission("/api/v1/documentCollection/save")
    public Result<?> update(@JsonBody DocumentChunk documentChunk) {
        boolean success = service.updateById(documentChunk);
        if (success){
            DocumentChunk documentChunk1 = documentChunkService.getById(documentChunk.getId());
            DocumentCollection knowledge = documentCollectionService.getById(documentChunk1.getKnowledgeId());
            if (knowledge == null) {
                return Result.fail(1, "知识库不存在");
            }
            DocumentStore documentStore = knowledge.toDocumentStore();
            if (documentStore == null) {
                return Result.fail(2, "知识库没有配置向量库");
            }
            // 设置向量模型
            Model model = modelService.getById(knowledge.getVectorEmbedModelId());
            if (model == null) {
                return Result.fail(3, "知识库没有配置向量模型");
            }
            EmbeddingModel embeddingModel = model.toEmbeddingModel();
            documentStore.setEmbeddingModel(embeddingModel);
            StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
            Document document = Document.of(documentChunk.getContent());
            document.setId(documentChunk.getId());
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("keywords", documentChunk.getMetadataKeyWords());
            metadata.put("questions", documentChunk.getMetadataQuestions());
            document.setMetadataMap(metadata);
            StoreResult result = documentStore.update(document, options); // 更新已有记录
            return Result.ok(result);
        }
        return Result.ok(false);
    }

    @PostMapping("removeChunk")
    @SaCheckPermission("/api/v1/documentCollection/remove")
    public Result<?> remove(@JsonBody(value = "id", required = true) BigInteger chunkId) {
        DocumentChunk docChunk =  documentChunkService.getById(chunkId);
        if (docChunk == null) {
            return Result.fail(1, "记录不存在");
        }
        DocumentCollection knowledge = documentCollectionService.getById(docChunk.getKnowledgeId());
        if (knowledge == null) {
            return Result.fail(2, "知识库不存在");
        }
        DocumentStore documentStore = knowledge.toDocumentStore();
        if (documentStore == null) {
            return Result.fail(3, "知识库没有配置向量库");
        }
        // 设置向量模型
        Model model = modelService.getById(knowledge.getVectorEmbedModelId());
        if (model == null) {
            return Result.fail(4, "知识库没有配置向量模型");
        }
        EmbeddingModel embeddingModel = model.toEmbeddingModel();
        documentStore.setEmbeddingModel(embeddingModel);
        StoreOptions options = StoreOptions.ofCollectionName(knowledge.getVectorStoreCollection());
        List<BigInteger> deleteList = new ArrayList<>();
        deleteList.add(chunkId);
        documentStore.delete(deleteList, options);
        documentChunkService.removeChunk(knowledge, chunkId);

        return super.remove(chunkId);
    }
}
