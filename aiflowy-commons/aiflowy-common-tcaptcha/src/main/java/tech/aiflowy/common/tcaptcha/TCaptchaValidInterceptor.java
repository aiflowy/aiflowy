package tech.aiflowy.common.tcaptcha;

import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.common.util.ResponseUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class TCaptchaValidInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        JSONObject jsonObject = (JSONObject) RequestUtil.readJsonObjectOrArray(request);
        if (jsonObject == null) {
            renderNotLogin(response);
            return false;
        }

        String ticket = jsonObject.getString("ticket");
        if (!StringUtils.hasText(ticket)) {
            renderNotLogin(response);
            return false;
        }

        String randstr = jsonObject.getString("randstr");
        if (!StringUtils.hasText(randstr)) {
            renderNotLogin(response);
            return false;
        }

        boolean validOk = TCaptchaUtil.validTicket(randstr, ticket);
        if (!validOk) {
            renderNotLogin(response);
            return false;
        }

        return true;
    }


    private static void renderNotLogin(HttpServletResponse response) {
        Result result = Result.fail(99, "验证数据不正确或已过期.");
        ResponseUtil.renderJson(response, result);
    }

}
