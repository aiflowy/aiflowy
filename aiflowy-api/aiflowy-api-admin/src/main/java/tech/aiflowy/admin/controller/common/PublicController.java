package tech.aiflowy.admin.controller.common;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import com.alibaba.fastjson2.JSON;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.captcha.tainai.CaptchaData;
import tech.aiflowy.system.entity.SysOption;
import tech.aiflowy.system.service.SysOptionService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共接口
 */
@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @Resource
    private ImageCaptchaApplication application;
    @Resource
    private SysOptionService sysOptionService;

    /**
     * 获取验证码
     */
    @RequestMapping(value = "/getCaptcha", produces = "application/json")
    public ApiResponse<ImageCaptchaVO> getCaptcha() {
        return application.generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    /**
     * 验证码校验
     */
    @PostMapping(value = "/check", produces = "application/json")
    public ApiResponse<String> checkCaptcha(@RequestBody CaptchaData data) {
        ApiResponse<?> response = application.matching(data.getId(), data.getData());
        if (!response.isSuccess()) {
            return ApiResponse.ofError("验证码错误");
        }
        return ApiResponse.ofSuccess(data.getId());
    }
}
