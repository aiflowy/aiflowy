package tech.aiflowy.ai.node;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alicp.jetcache.Cache;
import dev.tinyflow.core.util.OkHttpClientUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.utils.DocUtil;
import tech.aiflowy.common.constant.RedisKey;
import tech.aiflowy.common.util.SpringContextUtil;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component("giteeReader")
public class GiteeParseService implements ReadDocService {

    @Value("${node.gitee.appKey}")
    private String appKey;
    private static final Logger log = LoggerFactory.getLogger(GiteeParseService.class);
    @Resource(name = "defaultCache")
    private Cache<String, Object> defaultCache;

    @Override
    public String read(String fileName, InputStream is) {
        return getDocContent(fileName, is);
    }

    private String getDocContent(String fileName, InputStream is) {

        Object cache = defaultCache.get(RedisKey.DOC_NODE_CONTENT_KEY + fileName);

        if (cache != null) {
            return cache.toString();
        }
        String content;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try {
            byte[] b = DocUtil.readBytes(is);
            Map<Integer, byte[]> split = splitDocFile(DocUtil.getSuffix(fileName), b, 30);

            List<Callable<String>> tasks = new LinkedList<>();
            for (Map.Entry<Integer, byte[]> entry : split.entrySet()) {
                int index = entry.getKey();
                byte[] splitBytes = entry.getValue();
                tasks.add(() -> splitContent(index + "-" + fileName, splitBytes));
            }
            // 提交所有任务并等待完成
            List<Future<String>> futures = executor.invokeAll(tasks);
            StringBuilder res = new StringBuilder();
            for (Future<String> future : futures) {
                String call = future.get();
                if (StrUtil.isEmpty(call)) {
                    throw new RuntimeException("读取文件任务失败：" + call);
                }
                res.append(call).append("\n");
            }
            content = res.toString();

            defaultCache.put(RedisKey.DOC_NODE_CONTENT_KEY + fileName, content);
        } catch (Exception e) {
            log.error("读取文档内容失败：", e);
            throw new RuntimeException("读取文档内容失败：", e);
        } finally {
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        return content;
    }

    private String giteeParse(String fileName, byte[] bytes) {
        String url = "https://ai.gitee.com/v1/async/documents/parse";
        // 创建多部分请求体
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        builder.addFormDataPart("model", "PDF-Extract-Kit-1.0");
        builder.addFormDataPart("file", fileName,
                RequestBody.create(bytes, null));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + appKey)
                .post(requestBody).build();

        OkHttpClient okHttpClient = OkHttpClientUtil.buildDefaultClient();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            if (response.body() == null) {
                throw new RuntimeException("返回内容为空");
            }
            String jsonStr = response.body().string();
            JSONObject object = JSON.parseObject(jsonStr);
            log.info("读取文件接口返回：{}", jsonStr);
            String error = object.getString("error");
            if (StrUtil.isNotEmpty(error)) {
                throw new RuntimeException(object.getString("message"));
            }
            if ("failure".equals(object.getString("status"))) {
                String msg = "请求读取文件[" + fileName + "],失败：" + jsonStr;
                log.info(msg);
                throw new RuntimeException(msg);
            } else {
                return object.getString("task_id");
            }
        } catch (Exception e) {
            log.error("请求读取文件失败：", e);
            throw new RuntimeException(e);
        }
    }

    private String giteeParseResult(String taskId) {
        String getTaskUrl = "https://ai.gitee.com/v1/task/" + taskId;
        Request.Builder builder = new Request.Builder()
                .url(getTaskUrl);
        builder.addHeader("Authorization", "Bearer " + appKey);
        Request request = builder.build();
        OkHttpClient client = OkHttpClientUtil.buildDefaultClient();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (response.body() == null) {
                throw new RuntimeException("返回内容为空");
            }
            String jsonStr = response.body().string();
            JSONObject object = JSON.parseObject(jsonStr);
            if ("success".equals(object.getString("status"))) {
                JSONArray segments = object.getJSONObject("output").getJSONArray("segments");
                StringBuilder md = new StringBuilder();
                for (Object segment : segments) {
                    JSONObject json = (JSONObject) segment;
                    md.append(json.getString("content"));
                }
                return md.toString();
            } else {
                System.out.println(taskId + " >>>>>>>>> " + object);
            }
        } catch (Exception e) {
            log.error("请求失败：", e);
            throw new RuntimeException(e);
        }
        return "waiting";
    }


    private Map<Integer, byte[]> splitDocFile(String suffix, byte[] bytes, int splitSize) {
        if ("pdf".equals(suffix)) {
            return DocUtil.splitPdf(bytes, splitSize);
        } else {
            // 暂不处理word文档
            Map<Integer, byte[]> res = new HashMap<>();
            res.put(1, bytes);
            return res;
        }
    }

    private String splitContent(String fileName, byte[] b) {
        String taskId = giteeParse(fileName, b);
        while (true) {
            ThreadUtil.sleep(1000);
            String result = giteeParseResult(taskId);
            if (!"waiting".equals(result)) {
                // 去掉 HTML 标签，![images/xx](xxx)的内容，提取纯文本
                return handleMdContent(result);
            }
        }
    }

    private String handleMdContent(String content) {
        String txt = content.replaceAll("<[^>]*>", " ");
        txt = txt.replaceAll("!\\[images[^>]*\\)", " ");
        return txt;
    }
}
