package tech.aiflowy.ai.controller;

import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.entity.AiBotApiKey;
import tech.aiflowy.ai.service.AiBotApiKeyService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.aiflowy.common.annotation.UsePermission;
import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import java.math.BigInteger;
import tech.aiflowy.common.web.exceptions.BusinessException;
import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * bot apiKey 表 控制层。
 *
 * @author ArkLight
 * @since 2025-07-18
 */
@RestController
@RequestMapping("/api/v1/aiBotApiKey")
@SaIgnore
// @UsePermission(moduleName = "/api/v1/aiBot")
public class AiBotApiKeyController extends BaseCurdController<AiBotApiKeyService, AiBotApiKey> {
    public AiBotApiKeyController(AiBotApiKeyService service) {
        super(service);
    }

    @PostMapping("addKey")
    public Result addKey(@JsonBody(value = "botId",required = true)BigInteger botId){

        if (botId == null) {
            throw new BusinessException("botId不能为空");
        }

        String apiKey = service.generateApiKeyByBotId(botId);
        return Result.success(apiKey);
    }

    @GetMapping("test")
    public Result testDecode(@RequestParam String apiKey){

        BigInteger botId = service.decryptApiKey(apiKey);
        return Result.success(botId);

    }


}