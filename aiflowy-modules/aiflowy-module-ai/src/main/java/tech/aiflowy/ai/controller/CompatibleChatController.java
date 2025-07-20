package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.agentsflex.core.llm.ChatOptions;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.prompt.TextPrompt;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.AiBot;
import tech.aiflowy.ai.entity.AiBotKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.entity.openAi.error.OpenAiErrorResponse;
import tech.aiflowy.ai.entity.openAi.request.ChatMessage;
import tech.aiflowy.ai.entity.openAi.request.OpenAiChatRequest;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.common.ai.MySseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.List;

/**
 * 兼容 openAi api 的，调用 bot 的控制器
 */
@RestController
@SaIgnore
public class CompatibleChatController {


    @Resource
    private AiBotApiKeyService aiBotApiKeyService;

    @Resource
    private AiBotService aiBotService;

    @Resource
    private AiBotLlmService aiBotLlmService;

    @Resource
    private AiLlmService aiLlmService;

    @Resource
    private AiBotPluginsService aiBotPluginsService;

    @Resource
    private AiPluginToolService aiPluginToolService;

    @Resource
    private AiBotKnowledgeService aiBotKnowledgeService;
    ;

    @Resource
    private AiKnowledgeService aiKnowledgeService;

    @Resource
    private AiBotWorkflowService aiBotWorkflowService;

    @Resource
    private AiWorkflowService aiWorkflowService;


    @PostMapping("/v1/chat/completions")
    public Object chat(@RequestBody OpenAiChatRequest params, HttpServletRequest request, HttpServletResponse response) {

        // 校验 apiKey
        String authorization = request.getHeader("Authorization");

        if (!StringUtils.hasLength(authorization)) {
            return new OpenAiErrorResponse("Invalid token", "invalid_request_error", "authorization", "401");
        }

        String apiKey = authorization.replace("Bearer ", "");
        if (!StringUtils.hasLength(apiKey)) {
            return new OpenAiErrorResponse("Invalid token", "invalid_request_error", "authorization", "401");
        }

        // 校验 messages
        List<ChatMessage> messages = params.getMessages();
        if (messages == null || messages.isEmpty()) {
            return new OpenAiErrorResponse("Bad Request", "messages can not be null or empty", "messages", "400");
        }


        BigInteger botId = null;
        try {
            botId = aiBotApiKeyService.decryptApiKey(apiKey);
        } catch (Exception e) {
            return new OpenAiErrorResponse("Invalid token", "invalid_request_error", "authorization", "401");
        }

        AiBot aiBot = aiBotService.getById(botId);
        if (aiBot == null) {
            return new OpenAiErrorResponse("Bot Not Found", "resource_not_found_error", null, "404");
        }

        // 校验 llm
        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());
        if (aiLlm == null) {
            return new OpenAiErrorResponse("Llm Not Found", "resource_not_found_error", null, "404");
        }

        Boolean stream = params.getStream() ? params.getStream() : true;

        if (stream) {
            return handleStreamChat(aiBot,aiLlm, params, request, response);
        } else {
            return handleNotStreamChat(aiBot,aiLlm, params, request, response);
        }

    }

    private Object handleNotStreamChat(AiBot aiBot,AiLlm aiLlm, OpenAiChatRequest params, HttpServletRequest request, HttpServletResponse response) {

        ChatOptions chatOptions = buildChatOptions(params, aiBot, aiLlm);

        return null;
    }

    private SseEmitter handleStreamChat(AiBot aiBot,AiLlm aiLlm, OpenAiChatRequest params, HttpServletRequest request, HttpServletResponse response) {

        MySseEmitter mySseEmitter = new MySseEmitter(1000 * 60 * 300L);
        ChatOptions chatOptions = buildChatOptions(params, aiBot, aiLlm);

        Llm llm = aiLlm.toLlm();

        return mySseEmitter;

    }

    private ChatOptions buildChatOptions(OpenAiChatRequest params, AiBot aiBot,AiLlm aiLlm) {
        ChatOptions chatOptions = new ChatOptions();
        chatOptions.setEnableThinking(params.getEnableThinking() ? params.getEnableThinking() : true);
        chatOptions.setSeed(params.getSeed().toString());
        chatOptions.setStop(params.getStop());
        chatOptions.setTemperature(params.getTemperature());
        chatOptions.setTopK(params.getTopK());
        chatOptions.setTopP(params.getTopP());
        chatOptions.setMaxTokens(params.getMaxTokens());
        chatOptions.setModel(aiLlm.getLlmModel());

        return chatOptions;
    }


}
