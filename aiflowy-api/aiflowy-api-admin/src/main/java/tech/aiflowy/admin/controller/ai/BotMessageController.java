package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaIgnore;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.ToolMessage;
import com.agentsflex.core.util.Maps;
import com.agentsflex.core.util.StringUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.ai.entity.BotMessage;
import tech.aiflowy.ai.service.BotMessageService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bot 消息记录表 控制层。
 *
 * @author michael
 * @since 2024-11-04
 */
@RestController
@RequestMapping("/api/v1/aiBotMessage")
@UsePermission(moduleName = "/api/v1/aiBot")
public class BotMessageController extends BaseCurdController<BotMessageService, BotMessage> {
    private final BotMessageService botMessageService;

    public BotMessageController(BotMessageService service, BotMessageService botMessageService) {
        super(service);
        this.botMessageService = botMessageService;
    }


    @GetMapping("list")
    @Override
    public Result list(BotMessage entity, Boolean asTree, String sortKey, String sortType) {

        if (entity.getBotId() == null) {
            return Result.fail("查询失败");
        }

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BotMessage::getBotId, entity.getBotId());
        queryWrapper.eq(BotMessage::getAccountId, SaTokenUtil.getLoginAccount().getId());
        queryWrapper.eq(BotMessage::getSessionId, entity.getSessionId());
        queryWrapper.orderBy(BotMessage::getCreated, true);

        List<BotMessage> list = service.list(queryWrapper);

        if (list == null || list.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        List<Maps> maps = new ArrayList<>();
        for (BotMessage botMessage : list) {
            Message message = botMessage.getContentAsMessage();
            if (message instanceof ToolMessage) {
               continue;
            }
            if ("function".equals(botMessage.getRole()) || !StringUtil.hasText(message.getTextContent())){
                continue;
            }
            maps.add(Maps.of("id", botMessage.getId())
                    .set("content", message.getTextContent())
                    .set("role", botMessage.getRole())
                    .set("options", botMessage.getOptions())
                    .set("created", botMessage.getCreated().getTime())
                    .set("updateAt", botMessage.getCreated().getTime())
            );
        }
        return Result.ok(maps);
    }


    @Override
    protected Result<?> onSaveOrUpdateBefore(BotMessage entity, boolean isSave) {
        entity.setAccountId(SaTokenUtil.getLoginAccount().getId());
        return super.onSaveOrUpdateBefore(entity, isSave);
    }


    @GetMapping("messageList")
    @SaIgnore
    public Result<?> messageList(@RequestParam(value = "botId") String botId,
                              @RequestParam(value = "sessionId") String sessionId,
                              @RequestParam(value = "tempUserId", required = false) String tempUserId,
                              @RequestParam(value = "tempUserSession", required = false) String tempUserSessionId
    ) {

        return botMessageService.messageList(botId, sessionId,  tempUserId, tempUserSessionId);
    }

    @PostMapping("removeMsg")
    public Result<Boolean> removeMsg(@JsonBody(value = "botId", required = true) String botId,
                                     @JsonBody(value = "sessionId", required = true) String sessionId
    ) {

        return Result.ok(botMessageService.removeMsg(botId, sessionId));
    }


}
