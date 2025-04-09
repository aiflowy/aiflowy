package tech.aiflowy.common.controller;

import tech.aiflowy.ai.entity.AiChatTopic;
import tech.aiflowy.ai.service.AiChatMessageService;
import tech.aiflowy.ai.service.AiChatTopicService;
import tech.aiflowy.common.ai.ChatManager;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import tech.aiflowy.common.satoken.util.SaTokenUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/v1/ai/")
public class AiController {
    @Resource
    private AiChatTopicService topicService;
    @Resource
    private AiChatMessageService messageService;

    @PostMapping("chat")
    public SseEmitter chat(@JsonBody("prompt") String prompt, @JsonBody("topicId") BigInteger topicId, HttpServletResponse response) {
        response.setContentType("text/event-stream");

        AiChatTopic topic = topicService.getOrCreateDefaultTopic(topicId, SaTokenUtil.getLoginAccount().getId());
        AiTopicMemory memory = new AiTopicMemory(topic.getId(), messageService);

//        List<Function> functions = FunctionsManager.getInstance().getFunctions(prompt);
//        if (functions != null && !functions.isEmpty()) {
//            return FunctionsManager.getInstance().call(prompt, functions, memory);
//        }
        return ChatManager.getInstance().sseEmitter(prompt, memory);
    }

}
