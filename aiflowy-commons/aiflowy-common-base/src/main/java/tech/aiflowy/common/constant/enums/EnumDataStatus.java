package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

/**
 * 通用数据状态
 */
@DictDef(name = "数据权限类型", code = "dataStatus", keyField = "code", labelField = "text")
public enum EnumDataStatus {

    UNAVAILABLE(0, "未启用"),
    AVAILABLE(1, "已启用"),
    ;

    private Integer code;
    private String text;

    EnumDataStatus(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumDataStatus getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumDataStatus type : EnumDataStatus.values()) {
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
