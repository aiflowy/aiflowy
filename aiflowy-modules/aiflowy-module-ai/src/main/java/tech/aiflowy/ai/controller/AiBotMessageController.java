package tech.aiflowy.ai.controller;

import org.springframework.web.bind.annotation.RequestParam;
import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.ai.service.AiBotMessageService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import com.agentsflex.core.util.Maps;
import com.agentsflex.core.util.StringUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Bot 消息记录表 控制层。
 *
 * @author michael
 * @since 2024-11-04
 */
@RestController
@RequestMapping("/api/v1/aiBotMessage")
public class AiBotMessageController extends BaseCurdController<AiBotMessageService, AiBotMessage> {
    private final AiBotMessageService aiBotMessageService;

    public AiBotMessageController(AiBotMessageService service, AiBotMessageService aiBotMessageService) {
        super(service);
        this.aiBotMessageService = aiBotMessageService;
    }


    @GetMapping("list")
    @Override
    public Result list(AiBotMessage entity, Boolean asTree, String sortKey, String sortType) {

        if (entity.getBotId() == null || StringUtil.noText(entity.getSessionId())) {
            return Result.fail();
        }

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(AiBotMessage::getBotId, entity.getBotId());
        queryWrapper.eq(AiBotMessage::getAccountId, SaTokenUtil.getLoginAccount().getId());
        queryWrapper.eq(AiBotMessage::getSessionId, entity.getSessionId());
        queryWrapper.orderBy(AiBotMessage::getCreated, true);

        List<AiBotMessage> list = service.list(queryWrapper);

        if (list == null || list.isEmpty()) {
            return Result.fail();
        }

        List<Maps> maps = new ArrayList<>();
        for (AiBotMessage aiBotMessage : list) {
            maps.add(Maps.of("id", aiBotMessage.getId())
                    .set("content", aiBotMessage.getContent())
                    .set("role", aiBotMessage.getRole())
                    .set("created", aiBotMessage.getCreated().getTime())
                    .set("updateAt", aiBotMessage.getCreated().getTime())
            );
        }
        return Result.success(maps);
    }


    @Override
    protected Result onSaveOrUpdateBefore(AiBotMessage entity, boolean isSave) {
        entity.setAccountId(SaTokenUtil.getLoginAccount().getId());
        return super.onSaveOrUpdateBefore(entity, isSave);
    }



    @GetMapping("messageList")
    public Result messageList(@RequestParam(value = "botId") String botId,
                              @RequestParam(value = "sessionId") String sessionId,
                              @RequestParam(value = "isExternalMsg") int isExternalMsg
                              ) {

        return aiBotMessageService.messageList(botId, sessionId, isExternalMsg);
    }


}