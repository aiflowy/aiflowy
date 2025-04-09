package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiBotLlm;
import tech.aiflowy.ai.service.AiBotLlmService;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  控制层。
 *
 * @author michael
 * @since 2024-08-28
 */
@RestController
@RequestMapping("/api/v1/aiBotLlm")
public class AiBotLlmController extends BaseCurdController<AiBotLlmService, AiBotLlm> {
    public AiBotLlmController(AiBotLlmService service) {
        super(service);
    }
}