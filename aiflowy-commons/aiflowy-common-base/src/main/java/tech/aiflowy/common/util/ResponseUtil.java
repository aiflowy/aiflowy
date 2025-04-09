package tech.aiflowy.common.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    public static void renderJson(HttpServletResponse response, Object object) {
        String json = JSON.toJSONString(object);
        renderJson(response, json);
    }

    public static void renderJson(HttpServletResponse response, String jsonString) {
        response.setContentType("application/json; charset=utf-8");
        try {
            response.getWriter().write(jsonString);
        } catch (IOException e) {
            //ignore
        }
    }
}
