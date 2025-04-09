package tech.aiflowy.common.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Map;

public class RequestUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUtil.class);

    private static final String jsonCacheKey = "__$JSONObjectOrArray";

    public static Object readJsonObjectOrArray(HttpServletRequest request) {
        Object jsonObjectOrArray = request.getAttribute(jsonCacheKey);
        if (jsonObjectOrArray == null) {
            String body = readBodyString(request);
            jsonObjectOrArray = JSON.parse(body);
            request.setAttribute(jsonCacheKey, jsonObjectOrArray);
        }
        return jsonObjectOrArray;
    }


    public static String readBodyString(HttpServletRequest request) {
        try {
            String ce = request.getCharacterEncoding();
            InputStreamReader reader = new InputStreamReader(request.getInputStream(), ce != null ? ce : "UTF-8");
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            for (int num; (num = reader.read(buf, 0, buf.length)) != -1; ) {
                sb.append(buf, 0, num);
            }
            return sb.toString();
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }


    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-requested-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }

        return ip;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }


    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }


    public static Boolean getParamAsBoolean(Map<String, String[]> parameters, String key) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        String[] strings = parameters.get(key);
        if (strings == null || strings.length == 0) {
            return null;
        }

        return "true".equalsIgnoreCase(strings[0]);
    }


    public static String getParamAsString(String key) {
        return getParamAsString(getRequest().getParameterMap(), key);
    }


    public static String getParamAsString(Map<String, String[]> parameters, String key) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        String[] strings = parameters.get(key);
        if (strings == null || strings.length == 0) {
            return null;
        }

        String trimmed = strings[0].trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        return trimmed;
    }


    public static BigInteger getParamAsBigInteger(Map<String, String[]> parameters, String key) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        String[] strings = parameters.get(key);
        if (strings == null || strings.length == 0) {
            return null;
        }

        return new BigInteger(strings[0]);
    }

    public static BigInteger getParamAsBigInteger(String key) {
        return getParamAsBigInteger(getRequest().getParameterMap(), key);
    }


    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取 HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取 HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

}
