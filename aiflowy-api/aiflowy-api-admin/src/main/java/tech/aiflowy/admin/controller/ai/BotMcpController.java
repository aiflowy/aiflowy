package tech.aiflowy.admin.controller.ai;

import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.entity.BotMcp;
import tech.aiflowy.ai.service.BotMcpService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2026-01-05
 */
@RestController
@RequestMapping("/api/v1/botMcp")
@UsePermission(moduleName = "/api/v1/bot")
public class BotMcpController extends BaseCurdController<BotMcpService, BotMcp> {
    public BotMcpController(BotMcpService service) {
        super(service);
    }


    @PostMapping("updateBotMcpToolIds")
    public Result<?> save(@JsonBody("botId") BigInteger botId,
                          @JsonBody("mcpSelectedData")  List<Map<String, List<List<String>>>> mcpSelectedData) {
        service.updateBotMcpToolIds(botId, mcpSelectedData);
        return Result.ok();
    }
}