package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiChatTopic;
import tech.aiflowy.ai.service.AiChatTopicService;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 话题表 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiChatTopic")
public class AiChatTopicController extends BaseCurdController<AiChatTopicService, AiChatTopic> {
    public AiChatTopicController(AiChatTopicService service) {
        super(service);
    }
}