package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.mapper.AiLlmMapper;
import tech.aiflowy.ai.service.AiOllamaService;
import tech.aiflowy.ai.vo.OllamaModel;
import tech.aiflowy.common.domain.Result;
import com.agentsflex.core.llm.client.HttpClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.paginate.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;


/**
 *  业务层
 *
 * @author wangGangQiang
 * @since 2025-03-13
 */
@Service
public class AiOllamaServiceImpl implements AiOllamaService {

    @Autowired
    AiLlmMapper aiLlmMapper;

    @Value("${aiflowy.ollama.host}")
    private String ollamaApiUrl;
    // 正则表达式：匹配 http://IP:PORT 格式的地址
    private static final String URL_PATTERN =
            "^(https?://)?" +  // 可选的协议部分（http 或 https）
                    "((www\\.)?" +  // 可选的 www. 前缀
                    "([a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+|" +  // 支持域名
                    "localhost|" +  // 支持 localhost
                    "(\\d{1,3}\\.){3}\\d{1,3}))" +  // 支持 IPv4 地址
                    "(:\\d{1,5})?" +  // 可选的端口号
                    "(/.*)?$";  // 可选的路径部分
    // 预编译正则表达式
    private static final Pattern pattern = Pattern.compile(URL_PATTERN);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public Result getLargeModels(String modelName,Integer current,Integer pageSize) {
        if (current == null){
            current = 1;
        }
        if (pageSize == null){
            pageSize = 9999;
        }
        String tempUrl;
        // 定义 API 地址
        tempUrl = ollamaApiUrl + "/api/tags";
        List<OllamaModel> models = new ArrayList<>();
        HttpClient httpClient = new HttpClient();
        String jsonString = httpClient.get(tempUrl);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        if (jsonObject == null){
            return Result.success();
        }
        // 查询当前的模型是否已经被用户加入了大模型中
        List<AiLlm> aiLlms = aiLlmMapper.selectAll();
        JSONArray modelsJsonArray = jsonObject.getJSONArray("models");
        for (int i = 0; i < modelsJsonArray.size(); i++) {
            JSONObject modalJsonObject = modelsJsonArray.getJSONObject(i);
            OllamaModel ollamaModel = modalJsonObject.toJavaObject(OllamaModel.class);
            ollamaModel.setApiUrl(tempUrl);
            OllamaModel ollamaModel1 = hasJoinModel(aiLlms, ollamaModel);
            // 如果不为空就是条件查询
            if (StringUtils.isNotEmpty(modelName)){
                if (ollamaModel.getName().contains(modelName)){
                    models.add(ollamaModel1);
                }
            } else {
                models.add(ollamaModel1);
            }

        }
        Page page = new Page();
        if(models != null){
            page = new Page(models, current, pageSize, models.size());
        }
        return Result.success(page);
    }


    @Override
    public Result getOllamaModels() {
        String apiUrl = "http://api.ollama.com/v1/models";
        HttpClient httpClient = new HttpClient();
        String jsonString = httpClient.get(apiUrl);
        System.out.println(jsonString);
        return null;
    }


    @Override
    public SseEmitter installModel(String modelApiUrl, String modelName) {
        SseEmitter emitter = new SseEmitter(0L); // 无超时限制

        executor.submit(() -> {
            try {
                // 调用通用方法发送请求
                Object response = AiOllamaServiceImpl.sendHttpRequest(ollamaApiUrl, "/api/pull", "POST", modelName);

                // 处理响应流
                if (response instanceof BufferedReader) {
                    BufferedReader reader = (BufferedReader) response;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 将进度信息推送到前端
                        emitter.send(SseEmitter.event().data(line));
                    }
                }

                // 完成
                emitter.send(SseEmitter.event().data("{\"status\": \"complete\"}"));
                emitter.complete(); // 完成 SSE 连接
            } catch (Exception e) {
                emitter.completeWithError(e); // 发送错误
            }
        });

        return emitter;
    }
    @Override
    public boolean deleteModel(String modelApiUrl, String modelName) {
        try {
            // 调用通用方法发送请求
            Object response = AiOllamaServiceImpl.sendHttpRequest(ollamaApiUrl, "/api/delete", "DELETE", modelName);
            // 返回删除结果
            return (boolean) response;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 校验传入的地址是否符合 http://IP:PORT 格式
     *
     * @param address 待校验的地址字符串
     * @return 如果符合格式返回 true，否则返回 false
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false; // 空字符串直接返回 false
        }
        return pattern.matcher(address).matches();
    }


    /**
     * 通用的 HTTP 请求方法
     *
     * @param modelApiUrl  API 的基础 URL
     * @param endpoint     API 的路径（如 "/api/pull" 或 "/api/delete"）
     * @param method       HTTP 方法（如 "POST" 或 "DELETE"）
     * @param modelName    模型名称
     * @return 响应结果（如果是 DELETE 方法，返回布尔值；如果是 POST 方法，返回 BufferedReader）
     * @throws IOException 如果发生 I/O 错误
     */
    public static Object sendHttpRequest(String modelApiUrl, String endpoint, String method, String modelName) throws IOException, MalformedURLException {
        // 构造 URL
        URL url = new URL(modelApiUrl + endpoint);

        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 构造请求体
        String jsonInputString = "{\"name\": \"" + modelName + "\"}";

        // 发送请求体
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 获取响应码
        int responseCode = connection.getResponseCode();

        if ("DELETE".equalsIgnoreCase(method)) {
            // 处理 DELETE 请求
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                return true; // 删除成功
            } else {
                // 打印错误信息
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.err.println("错误信息: " + errorResponse.toString());
                }
                return false; // 删除失败
            }
        } else {
            // 处理 POST 请求
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                throw new IOException("HTTP 请求失败，状态码: " + responseCode);
            }
        }
    }

    /**
     * 判断当前大模型是否已经被用户添加到大模型中了
     * @param aiLlmList
     * @param ollamaModel
     * @return
     */
    public static OllamaModel  hasJoinModel(List<AiLlm> aiLlmList, OllamaModel ollamaModel){
        aiLlmList.stream()
                .anyMatch(item -> {
                    if (item.getTitle().equals(ollamaModel.getName())) {
                        ollamaModel.setHasJoinModel(1);
                        return true; // 找到匹配项，停止遍历
                    }
                    return false;
                });
        return ollamaModel;
    }
}
