package tech.aiflowy.publicapi.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.agentsflex.core.message.UserMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.Bot;
import tech.aiflowy.ai.entity.ChatRequestParams;
import tech.aiflowy.ai.service.BotService;
import tech.aiflowy.ai.service.impl.BotServiceImpl;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.core.chat.protocol.sse.ChatSseUtil;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.service.SysApiKeyService;

import javax.annotation.Resource;

/**
 * bot 接口
 */
@RestController
@RequestMapping("/public-api/bot")
public class PublicBotController {

    @Resource
    private BotService botService;
    @Resource
    private SysApiKeyService sysApiKeyService;

    /**
     * 根据id或别名获取bot详情
     */
    @GetMapping("/getByIdOrAlias")
    public Result<Bot> getByIdOrAlias(@NotBlank(message = "key不能为空") String key) {
        return Result.ok(botService.getDetail(key));
    }

    /**
     * 第三方调用聊天助手
     *
     * @return 返回SseEmitter对象，用于服务器向客户端推送聊天响应数据
     */
    @PostMapping("chat")
    public SseEmitter chat(@RequestBody ChatRequestParams chatRequestParams, HttpServletRequest request) {
        String apikey = request.getHeader(SysApiKey.KEY_Apikey);
        String requestURI = request.getRequestURI();
        if (!StringUtils.hasText(apikey)) {
           return ChatSseUtil.sendSystemError(null, "Apikey不能为空!");
        }
        sysApiKeyService.checkApikeyPermission(apikey, requestURI);
        BotServiceImpl.ChatCheckResult chatCheckResult = new BotServiceImpl.ChatCheckResult();
        int size = chatRequestParams.getMessages().size();
        String prompt = null;
        if (chatRequestParams.getMessages().get(size - 1) instanceof UserMessage) {
            prompt = ((UserMessage) chatRequestParams.getMessages().get(size - 1)).getContent();
        }
        // 前置校验：失败则直接返回错误SseEmitter
        SseEmitter errorEmitter = botService.checkChatBeforeStart(chatRequestParams.getBotId(), prompt, chatRequestParams.getConversationId(), chatCheckResult);
        if (errorEmitter != null) {
            return errorEmitter;
        }
        return botService.startPublicChat(chatRequestParams.getBotId(), prompt, chatRequestParams.getMessages(), chatCheckResult);
    }


}
