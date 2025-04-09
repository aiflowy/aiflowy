package tech.aiflowy.common.controller;

import tech.aiflowy.common.Consts;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tcaptcha.TCaptchaConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/public/")
public class PublicController {

    @Resource
    private TCaptchaConfig tCaptchaConfig;

    @GetMapping("tcaptcha")
    public Result tcaptcha() {
        return Result.success()
                .set("enable", tCaptchaConfig.getEnable())
                .set("appId", tCaptchaConfig.getCaptchaAppId());

    }

    @GetMapping("getDataScopeState")
    public Result getDataScopeState() {
        return Result.success()
                .set("enable", Consts.ENABLE_DATA_SCOPE);
    }
}
