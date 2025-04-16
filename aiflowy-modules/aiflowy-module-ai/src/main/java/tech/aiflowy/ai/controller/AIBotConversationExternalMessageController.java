package tech.aiflowy.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.service.AiBotConversationMessageService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;

import javax.annotation.Resource;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/conversation")
public class AIBotConversationExternalMessageController extends BaseCurdController<AiBotConversationMessageService, AiBotConversationMessage> {

    @Resource
    private AiBotConversationMessageService conversationMessageService;


    public AIBotConversationExternalMessageController(AiBotConversationMessageService service) {
        super(service);
    }

    @GetMapping("externalList")
    public Result externalList(@RequestParam(value = "botId") BigInteger botId) {

        return conversationMessageService.externalList(botId);
    }

    @GetMapping("deleteConversation")
    public Result deleteConversation(@RequestParam(value = "botId") String botId,
                                     @RequestParam(value = "sessionId") String sessionId
    ) {

        return conversationMessageService.deleteConversation(botId, sessionId);
    }

    @GetMapping("updateConversation")
    public Result updateConversation(@RequestParam(value = "botId") String botId,
                                     @RequestParam(value = "sessionId") String sessionId,
                                     @RequestParam(value = "title") String title
    ) {

        return conversationMessageService.updateConversation(botId, sessionId, title);
    }
}
