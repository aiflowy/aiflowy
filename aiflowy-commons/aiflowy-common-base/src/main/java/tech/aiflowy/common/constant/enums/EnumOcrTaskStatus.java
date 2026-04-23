package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "ocr任务状态", code = "ocrTaskStatus", keyField = "code", labelField = "text")
public enum EnumOcrTaskStatus {

    //任务状态0无任务1进行中2任务完成3任务失败
    NO_TASK(0, "-"),
    IN_PROGRESS(1, "进行中"),
    COMPLETED(2, "任务完成"),
    FAILED(3, "任务失败"),
    ;

    private Integer code;
    private String text;

    EnumOcrTaskStatus(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumOcrTaskStatus getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumOcrTaskStatus type : EnumOcrTaskStatus.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new RuntimeException("内容类型非法");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }}
