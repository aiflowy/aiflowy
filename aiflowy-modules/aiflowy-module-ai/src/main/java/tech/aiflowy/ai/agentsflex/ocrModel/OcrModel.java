package tech.aiflowy.ai.agentsflex.ocrModel;

public interface OcrModel {

    String getText(String filePath);

    /**
     * 请求ocr，返回任务ID
     */
    String runAsync(String filePath);

    /**
     * 查询任务结果
     */
    ParseResult queryTask(String taskId);
}
