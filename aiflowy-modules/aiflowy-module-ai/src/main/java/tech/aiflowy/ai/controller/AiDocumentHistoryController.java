package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiDocumentHistory;
import tech.aiflowy.ai.service.AiDocumentHistoryService;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiDocumentHistory")
public class AiDocumentHistoryController extends BaseCurdController<AiDocumentHistoryService, AiDocumentHistory> {
    public AiDocumentHistoryController(AiDocumentHistoryService service) {
        super(service);
    }
}