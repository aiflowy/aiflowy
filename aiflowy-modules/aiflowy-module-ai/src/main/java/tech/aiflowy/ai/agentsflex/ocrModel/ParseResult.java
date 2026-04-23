package tech.aiflowy.ai.agentsflex.ocrModel;

/**
 * 统一的文档解析结果
 * 不管哪个厂商返回什么格式，最终都转换成这个结构
 */
public class ParseResult {

    /**
     * @see tech.aiflowy.common.constant.enums.EnumOcrTaskStatus
     */
    private Integer status;
    // 解析出的文本内容
    private String content;
    // 错误信息（失败时）
    private String errorMsg;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
