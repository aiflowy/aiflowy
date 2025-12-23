package tech.aiflowy.admin.controller.ai;

import tech.aiflowy.ai.entity.DocumentHistory;
import tech.aiflowy.ai.service.DocumentHistoryService;
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
public class DocumentHistoryController extends BaseCurdController<DocumentHistoryService, DocumentHistory> {
    public DocumentHistoryController(DocumentHistoryService service) {
        super(service);
    }
}