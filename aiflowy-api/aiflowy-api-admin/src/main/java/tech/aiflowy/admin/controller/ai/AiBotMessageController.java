package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaIgnore;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.message.ToolMessage;
import com.agentsflex.core.util.Maps;
import com.agentsflex.core.util.StringUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.ai.service.AiBotMessageService;
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
public class AiBotMessageController extends BaseCurdController<AiBotMessageService, AiBotMessage> {
    private final AiBotMessageService aiBotMessageService;

    public AiBotMessageController(AiBotMessageService service, AiBotMessageService aiBotMessageService) {
        super(service);
        this.aiBotMessageService = aiBotMessageService;
    }


    @GetMapping("list")
    @Override
    public Result list(AiBotMessage entity, Boolean asTree, String sortKey, String sortType) {

        if (entity.getBotId() == null) {
            return Result.fail("查询失败");
        }

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(AiBotMessage::getBotId, entity.getBotId());
        queryWrapper.eq(AiBotMessage::getAccountId, SaTokenUtil.getLoginAccount().getId());
        queryWrapper.eq(AiBotMessage::getSessionId, entity.getSessionId());
        queryWrapper.orderBy(AiBotMessage::getCreated, true);

        List<AiBotMessage> list = service.list(queryWrapper);

        if (list == null || list.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        List<Maps> maps = new ArrayList<>();
        for (AiBotMessage aiBotMessage : list) {
            Message message = aiBotMessage.getContentAsMessage();
            if (message instanceof ToolMessage) {
               continue;
            }
            if ("function".equals(aiBotMessage.getRole()) || !StringUtil.hasText(message.getTextContent())){
                continue;
            }
            maps.add(Maps.of("id", aiBotMessage.getId())
                    .set("content", message.getTextContent())
                    .set("role", aiBotMessage.getRole())
                    .set("options", aiBotMessage.getOptions())
                    .set("created", aiBotMessage.getCreated().getTime())
                    .set("updateAt", aiBotMessage.getCreated().getTime())
            );
        }
        return Result.ok(maps);
    }


    @Override
    protected Result<?> onSaveOrUpdateBefore(AiBotMessage entity, boolean isSave) {
        entity.setAccountId(SaTokenUtil.getLoginAccount().getId());
        return super.onSaveOrUpdateBefore(entity, isSave);
    }


    @GetMapping("messageList")
    @SaIgnore
    public Result<?> messageList(@RequestParam(value = "botId") String botId,
                              @RequestParam(value = "sessionId") String sessionId,
                              @RequestParam(value = "isExternalMsg") int isExternalMsg,
                              @RequestParam(value = "tempUserId", required = false) String tempUserId,
                              @RequestParam(value = "tempUserSession", required = false) String tempUserSessionId
    ) {

        return aiBotMessageService.messageList(botId, sessionId, isExternalMsg, tempUserId, tempUserSessionId);
    }

    @PostMapping("removeMsg")
    public Result<Boolean> removeMsg(@JsonBody(value = "botId", required = true) String botId,
                                     @JsonBody(value = "sessionId", required = true) String sessionId,
                                     @JsonBody(value = "isExternalMsg", required = true) int isExternalMsg
    ) {

        return Result.ok(aiBotMessageService.removeMsg(botId, sessionId, isExternalMsg));
    }


}
