package tech.aiflowy.ai.agentsflex.ocrModel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.common.ai.FileUploadUtil;
import tech.aiflowy.common.constant.enums.EnumOcrTaskStatus;
import tech.aiflowy.common.util.OkHttpUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GiteeOcrModel extends OcrModelConfig implements OcrModel {

    private static final int MAX_POLL_RETRY = 180;
    private static final long POLL_INTERVAL_SECONDS = 10;
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILED = "failer";
    private static final String STATUS_WAITING = "waiting";
    private static final String STATUS_PROCESSING = "in_progress";
    private static final Logger log = LoggerFactory.getLogger(GiteeOcrModel.class);

    private static final String QUERY_TASK_URL = "https://ai.gitee.com/v1/task/{task_id}";

    private final GiteeOcrModelConfig config;

    public GiteeOcrModel(GiteeOcrModelConfig giteeOcrModelConfig) {
        super();
        if (giteeOcrModelConfig == null) {
            throw new IllegalArgumentException("GiteeOcrModelConfig 配置不能为空");
        }
        this.config = giteeOcrModelConfig;
    }

    @Override
    public String getText(String filePath) {
        List<String> segments = getTextSegments(filePath);
        if (segments.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String segment : segments) {
            stringBuilder.append(segment);
        }
        return stringBuilder.toString();
    }

    public List<String> getTextSegments(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        Response uploadResponse = null;
        try {
            uploadResponse = uploadFile(filePath);
            JSONObject uploadResult = parseUploadResponse(uploadResponse);

            String taskId = uploadResult.getString("task_id");
            String initialStatus = uploadResult.getString("status");
            String taskGetUrl = uploadResult.getJSONObject("urls").getString("get");

            if (!STATUS_WAITING.equals(initialStatus) && !STATUS_PROCESSING.equals(initialStatus)) {
                handleNonWaitingStatus(uploadResult);
                return new ArrayList<>();
            }

            return pollTaskResultSegments(taskId, taskGetUrl);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("任务轮询被中断", e);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("OCR文件解析失败: " + e.getMessage(), e);
        } finally {
            if (uploadResponse != null) {
                uploadResponse.close();
            }
        }
    }

    private Response uploadFile(String filePath) throws IOException {
        Response response = OkHttpUtil.multipart(
                getFullUrl(),
                getHeaders(),
                FileUploadUtil.buildFileMultipartBody(filePath, getModel())
        );
        if (!response.isSuccessful()) {
            throw new BusinessException(
                    String.format("文件上传失败，状态码：%d，URL：%s", response.code(), getFullUrl())
            );
        }
        return response;
    }

    private JSONObject parseUploadResponse(Response response) throws IOException {
        if (response.body() == null) {
            throw new BusinessException("文件上传响应体为空");
        }
        String bodyContent = response.body().string();
        JSONObject jsonObject = JSON.parseObject(bodyContent);

        String taskId = jsonObject.getString("task_id");
        JSONObject urls = jsonObject.getJSONObject("urls");
        if (taskId == null || taskId.isEmpty()) {
            throw new BusinessException("未获取到 task_id，响应内容：" + bodyContent);
        }
        if (urls == null || urls.getString("get") == null) {
            throw new BusinessException("未获取到任务查询 URL，响应内容：" + bodyContent);
        }
        return jsonObject;
    }

    private void handleNonWaitingStatus(JSONObject uploadResult) {
        String status = uploadResult.getString("status");
        if (STATUS_FAILED.equals(status)) {
            String message = uploadResult.getString("message");
            throw new BusinessException("任务初始化失败：" + (message == null ? "未知错误" : message));
        }
    }

    private String pollTaskResult(String taskId, String taskGetUrl) throws InterruptedException, IOException {
        List<String> segments = pollTaskResultSegments(taskId, taskGetUrl);
        if (segments.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String segment : segments) {
            stringBuilder.append(segment);
        }
        return stringBuilder.toString();
    }

    private List<String> pollTaskResultSegments(String taskId, String taskGetUrl) throws InterruptedException, IOException {
        for (int retry = 1; retry <= MAX_POLL_RETRY; retry++) {
            TimeUnit.SECONDS.sleep(POLL_INTERVAL_SECONDS);

            String taskJsonBodyResult = OkHttpUtil.get(taskGetUrl, getHeaders());
            if (taskJsonBodyResult == null || taskJsonBodyResult.isEmpty()) {
                continue;
            }

            JSONObject resultJsonObject = JSON.parseObject(taskJsonBodyResult);
            System.out.println("解析任务状态>>>>>>>>>>：" + resultJsonObject);
            String currentStatus = resultJsonObject.getString("status");

            if (STATUS_SUCCESS.equals(currentStatus)) {
                JSONObject outputJson = resultJsonObject.getJSONObject("output");
                if (outputJson == null) {
                    return new ArrayList<>();
                }
                JSONArray segmentsArray = outputJson.getJSONArray("segments");
                if (segmentsArray != null && !segmentsArray.isEmpty()) {
                    List<String> segmentContents = new ArrayList<>(segmentsArray.size());
                    segmentsArray.forEach(item -> {
                        JSONObject segment = (JSONObject) item;
                        segmentContents.add(segment.getString("content"));
                    });
                    return segmentContents;
                }
                return new ArrayList<>();
            } else if (STATUS_FAILED.equals(currentStatus)) {
                JSONObject output = resultJsonObject.getJSONObject("output");
                String errorMsg = output != null ? output.getString("error") : "未知错误";
                throw new BusinessException("文件解析失败：" + errorMsg);
            } else if (!STATUS_WAITING.equals(currentStatus) && !STATUS_PROCESSING.equals(currentStatus)) {
                throw new BusinessException("未知任务状态：" + currentStatus);
            }
        }
        throw new BusinessException("任务轮询超时，最大重试次数：" + MAX_POLL_RETRY);
    }

    @Override
    public String runAsync(String filePath) {
        try {
            Response response = uploadFile(filePath);
            JSONObject res = parseUploadResponse(response);
            return res.getString("task_id");
        } catch (Exception e) {
            log.error("请求OCR模型失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public ParseResult queryTask(String taskId) {
        ParseResult res = new ParseResult();
        String taskGetUrl = QUERY_TASK_URL.replace("{task_id}", taskId);
        log.info("查询任务状态：{}", taskGetUrl);
        String taskJsonBodyResult = OkHttpUtil.get(taskGetUrl, getHeaders());
        log.info("查询任务状态结果：{}", taskJsonBodyResult);
        JSONObject resObj = JSON.parseObject(taskJsonBodyResult);
        String currentStatus = resObj.getString("status");
        if (STATUS_SUCCESS.equals(currentStatus)) {
            StringBuilder content = new StringBuilder();
            JSONObject outputJson = resObj.getJSONObject("output");
            if (outputJson != null) {
                JSONArray segmentsArray = outputJson.getJSONArray("segments");
                if (segmentsArray != null && !segmentsArray.isEmpty()) {
                    for (Object object : segmentsArray) {
                        JSONObject segment = (JSONObject) object;
                        content.append(segment.getString("content"));
                    }
                }
            }
            res.setStatus(EnumOcrTaskStatus.COMPLETED.getCode());
            res.setContent(content.toString());
        } else if (STATUS_FAILED.equals(currentStatus)) {
            res.setStatus(EnumOcrTaskStatus.FAILED.getCode());
            JSONObject output = resObj.getJSONObject("output");
            String errorMsg = output != null ? output.getString("error") : "未知错误";
            res.setErrorMsg(errorMsg);
        } else {
            res.setStatus(EnumOcrTaskStatus.IN_PROGRESS.getCode());
        }
        return res;
    }
}
