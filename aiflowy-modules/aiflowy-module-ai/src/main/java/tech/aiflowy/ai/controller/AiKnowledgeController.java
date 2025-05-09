package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.ai.service.AiDocumentChunkService;
import tech.aiflowy.ai.service.AiKnowledgeService;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiKnowledge")
public class AiKnowledgeController extends BaseCurdController<AiKnowledgeService, AiKnowledge> {

    private final AiDocumentChunkService chunkService;
    private final AiLlmService llmService;

    public AiKnowledgeController(AiKnowledgeService service, AiDocumentChunkService chunkService, AiLlmService llmService) {
        super(service);
        this.chunkService = chunkService;
        this.llmService = llmService;
    }

    @Override
    protected Result onSaveOrUpdateBefore(AiKnowledge entity, boolean isSave) {
        if (isSave){
            entity.setCanUpdateEmbedLlm(true);
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @GetMapping("search")
    public Result search(@RequestParam BigInteger id, @RequestParam String keyword) {
        return service.search(id, keyword);
    }

}