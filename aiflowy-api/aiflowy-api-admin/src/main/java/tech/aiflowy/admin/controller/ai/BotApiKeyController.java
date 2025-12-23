package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.BotApiKey;
import tech.aiflowy.ai.service.BotApiKeyService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import java.math.BigInteger;
import java.util.List;

/**
 * bot apiKey 表 控制层。
 *
 * @author ArkLight
 * @since 2025-07-18
 */
@RestController
@RequestMapping("/api/v1/aiBotApiKey")
@UsePermission(moduleName = "/api/v1/aiBot")
public class BotApiKeyController extends BaseCurdController<BotApiKeyService, BotApiKey> {
    public BotApiKeyController(BotApiKeyService service) {
        super(service);
    }

    @PostMapping("addKey")
    @SaCheckPermission("/api/v1/aiBot/save")
    public Result<?> addKey(@JsonBody(value = "botId",required = true)BigInteger botId){

        if (botId == null) {
            throw new BusinessException("botId不能为空");
        }

        String apiKey = service.generateApiKeyByBotId(botId);
        return Result.ok(apiKey);
    }

    @SaIgnore
    @Override
    public Result<List<BotApiKey>> list(BotApiKey entity, Boolean asTree, String sortKey, String sortType) {

        Result<?> result = super.list(entity, asTree, sortKey, sortType);
        @SuppressWarnings("unchecked")
        List<BotApiKey> data = (List<BotApiKey>) result.getData();
        if (data != null && !data.isEmpty()) {
            data.forEach(item -> {
                item.setSalt(null);
            });
        }

        return Result.ok(data);
    }
}