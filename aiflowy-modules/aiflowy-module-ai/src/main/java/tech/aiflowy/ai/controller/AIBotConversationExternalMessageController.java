package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.alicp.jetcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.ai.entity.base.AiBotConversationMessageBase;
import tech.aiflowy.ai.service.AiBotConversationMessageService;
import tech.aiflowy.ai.service.AiBotMessageService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/conversation")
public class AIBotConversationExternalMessageController extends BaseCurdController<AiBotConversationMessageService, AiBotConversationMessage> {

    @Resource
    private AiBotConversationMessageService conversationMessageService;

    @Resource
    private AiBotMessageService aiBotMessageService;


    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;

    public AIBotConversationExternalMessageController(AiBotConversationMessageService service) {
        super(service);
    }

    @GetMapping("externalList")
    @SaIgnore
    public Result externalList(@RequestParam(value = "botId") BigInteger botId, @RequestParam(value = "tempUserId") String tempUserId) {
        boolean login = StpUtil.isLogin();
        if (login) {
            return conversationMessageService.externalList(botId);
        } else {
            List<AiBotConversationMessage> result = (List<AiBotConversationMessage>)cache.get(tempUserId + ":" + botId);
            System.out.println(result);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("cons", result);
            return Result.success(resultMap);
        }
    }

    @GetMapping("deleteConversation")
    @SaIgnore
    public Result deleteConversation(@RequestParam(value = "botId") String botId,
                                     @RequestParam(value = "sessionId") String sessionId,
                                     @RequestParam(value = "tempUserId") String tempUserId
    ) {

        boolean login = StpUtil.isLogin();
        if (login) {
            return conversationMessageService.deleteConversation(botId, sessionId);
        }

        List<AiBotConversationMessage> messages = (List<AiBotConversationMessage>) cache.get(tempUserId + ":" + botId);
        if (messages == null || messages.isEmpty()) {
            return Result.success();
        }
        List<AiBotConversationMessage> collect = messages.stream().filter(message -> !message.getSessionId().equals(sessionId)).collect(Collectors.toList());

        cache.put(tempUserId + ":" + botId, collect);

        return   Result.success() ;

    }

    @GetMapping("updateConversation")
    @SaIgnore
    public Result updateConversation(@RequestParam(value = "botId") String botId,
                                     @RequestParam(value = "sessionId") String sessionId,
                                     @RequestParam(value = "title") String title,
                                     @RequestParam(value = "tempUserId") String tempUserId
    ) {
        boolean login = StpUtil.isLogin();

        if (login) {
            return conversationMessageService.updateConversation(botId, sessionId, title);
        }

        List<AiBotConversationMessage > messages = (List<AiBotConversationMessage>) cache.get(tempUserId + ":" + botId);

        if (messages == null) {
            return Result.fail(500,"消息不存在");
        }

        messages.stream().filter(message -> message.getSessionId().equals(sessionId)).findFirst().ifPresent(message -> {
            message.setTitle(title);
        });

        cache.put(tempUserId + ":" + botId, messages);

        return Result.success();
    }

    @PostMapping("clearMessage")
    @SaIgnore
    public Result clearMessage(@JsonBody("botId") String botId,@JsonBody("sessionId") String sessionId, @JsonBody("tempUserId") String tempUserId) {
        boolean login = StpUtil.isLogin();

        if (login) {
            aiBotMessageService.removeMsg(botId,sessionId,1);
            return Result.success();
        }

        List<AiBotConversationMessage> messages = (List<AiBotConversationMessage>) cache.get(tempUserId + ":" + botId);
        if (messages == null || messages.isEmpty()) {
            return Result.success();
        }
        messages.forEach(message -> {
            if (message.getSessionId().equals(sessionId)) {
                message.setAiBotMessageList(new ArrayList<>());
            }
        });

        cache.put(tempUserId + ":" + botId, messages);

        return Result.success();
    }
}
