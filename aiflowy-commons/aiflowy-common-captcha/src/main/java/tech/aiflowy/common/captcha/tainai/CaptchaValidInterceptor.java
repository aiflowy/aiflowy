package tech.aiflowy.common.captcha.tainai;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.common.util.ResponseUtil;

@Component
public class CaptchaValidInterceptor implements HandlerInterceptor {

    @Resource
    private ImageCaptchaApplication application;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JSONObject jsonObject = (JSONObject) RequestUtil.readJsonObjectOrArray(request);
        String validToken = jsonObject.getString("validToken");
        if (validToken == null || validToken.isEmpty()) {
            renderNotLogin(response);
            return false;
        }
        boolean valid = ((SecondaryVerificationApplication) application).secondaryVerification(validToken);
        if (!valid) {
            renderNotLogin(response);
            return false;
        }
        return true;
    }

    private static void renderNotLogin(HttpServletResponse response) {
        Result result = Result.fail(99, "验证失败，请重试！");
        ResponseUtil.renderJson(response, result);
    }
}
