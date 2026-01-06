package tech.aiflowy.usercenter.controller.common;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.captcha.tainai.CaptchaData;

import javax.annotation.Resource;

/**
 * 公共接口
 */
@RestController
@RequestMapping("/userCenter/public")
public class UcPublicController {

    @Resource
    private ImageCaptchaApplication application;

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
