package tech.aiflowy.ai.entity;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.agentsflex.core.llm.client.OkHttpClientUtil;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.llm.functions.Parameter;
import com.agentsflex.core.util.StringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import okhttp3.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiPluginsFunction implements Function {

    private String name;
    private String desc;
    private String options;

    public AiPluginsFunction() {
    }

    public AiPluginsFunction(AiPlugins plugin) {
        this.name = plugin.getPluginName();
        this.desc = plugin.getPluginDesc();
        this.options = plugin.getOptions();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public Parameter[] getParameters() {
        List<Parameter> list = new ArrayList<>();
        JSONObject obj = JSON.parseObject(options);
        // params
        JSONArray params = obj.getJSONArray("params");
        for (Object param : params) {
            JSONObject paramObj = (JSONObject) param;
            Parameter parameter = new Parameter();
            parameter.setName(paramObj.getString("key"));
            parameter.setDescription(paramObj.getString("desc"));
            list.add(parameter);
        }
        // body
        JSONObject body = obj.getJSONObject("body");
        String bodyType = body.getString("type");
        if ("form-data".equals(bodyType) || "x-www-form-urlencoded".equals(bodyType)) {
            JSONArray content = body.getJSONArray("content");
            for (Object o : content) {
                JSONObject param = (JSONObject) o;
                Parameter parameter = new Parameter();
                parameter.setName(param.getString("key"));
                parameter.setDescription(param.getString("desc"));
                list.add(parameter);
            }
        }
        if ("JSON".equals(bodyType)) {
            JSONObject content = JSON.parseObject(body.getString("content"));
            for (Map.Entry<String, Object> entry : content.entrySet()) {
                Parameter parameter = new Parameter();
                parameter.setName(entry.getKey());
                list.add(parameter);
            }
        }
        return ArrayUtil.toArray(list, Parameter.class);
    }

    @SneakyThrows
    @Override
    public Object invoke(Map<String, Object> map) {

        JSONObject obj = JSON.parseObject(options);
        String method = obj.getString("method");
        String url = obj.getString("url");

        Map<String, Object> urlParam = new HashMap<>();
        JSONArray params = obj.getJSONArray("params");
        for (Object param : params) {
            JSONObject paramObj = (JSONObject) param;
            String key = paramObj.getString("key");
            String value = paramObj.getString("value");
            if (StringUtil.hasText(value)) {
                urlParam.put(key, value);
            } else {
                urlParam.put(key, map.get(key));
            }
        }
        String parametersString = mapToQueryString(urlParam);
        String newUrl = parametersString.isEmpty() ? url : url +
                (url.contains("?") ? "&" + parametersString : "?" + parametersString);

        Request.Builder reqBuilder = new Request.Builder()
                .url(newUrl);

        JSONArray headers = obj.getJSONArray("headers");
        for (Object header : headers) {
            JSONObject headerObj = (JSONObject) header;
            reqBuilder.addHeader(headerObj.getString("key"), headerObj.getString("value"));
        }

        if (StringUtil.noText(method) || "GET".equalsIgnoreCase(method)) {
            reqBuilder.method("GET", null);
        } else {
            reqBuilder.method(method.toUpperCase(), getRequestBody(obj,map));
        }
        Request build = reqBuilder.build();
        System.out.println("http plugin function calling >>>>>>>>>> " + build);
        OkHttpClient okHttpClient = OkHttpClientUtil.buildDefaultClient();
        Response response = okHttpClient.newCall(build).execute();
        ResponseBody body = response.body();
        if (body != null) {
            return body.string();
        }
        return null;
    }

    private RequestBody getRequestBody(JSONObject obj, Map<String, Object> invokeMap) {
        JSONObject body = obj.getJSONObject("body");
        String bodyType = body.getString("type");
        if ("JSON".equals(bodyType)) {
            return RequestBody.create(body.getString("content"), MediaType.parse("application/json"));
        }

        if ("x-www-form-urlencoded".equals(bodyType)) {
            Map<String, Object> map = new HashMap<>();
            JSONArray content = body.getJSONArray("content");
            for (Object o : content) {
                JSONObject param = (JSONObject) o;
                String key = param.getString("key");
                Object value = param.get("value");
                if (ObjectUtil.isNotEmpty(value)) {
                    map.put(key, value);
                } else {
                    map.put(key, invokeMap.get(key));
                }
            }
            String bodyString = mapToQueryString(map);
            return RequestBody.create(bodyString, MediaType.parse("application/x-www-form-urlencoded"));
        }

        if ("form-data".equals(bodyType)) {
            JSONArray content = body.getJSONArray("content");
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            for (Object o : content) {
                JSONObject param = (JSONObject) o;
                String key = param.getString("key");
                Object valueOfConfig = param.get("value");
                Object v = invokeMap.get(key);
                if (ObjectUtil.isNotEmpty(valueOfConfig)) {
                    builder.addFormDataPart(key, String.valueOf(valueOfConfig));
                } else {
                    builder.addFormDataPart(key, String.valueOf(v));
                }

            }
            return builder.build();
        }
        if ("raw".equals(bodyType)) {
            return RequestBody.create(body.getString("content"), null);
        }
        // none
        return RequestBody.create("", null);
    }


    public static String mapToQueryString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (StringUtil.noText(key)) {
                continue;
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(key.trim());
            stringBuilder.append("=");
            Object value = map.get(key);
            stringBuilder.append(value == null ? "" : urlEncode(value.toString().trim()));
        }
        return stringBuilder.toString();
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
