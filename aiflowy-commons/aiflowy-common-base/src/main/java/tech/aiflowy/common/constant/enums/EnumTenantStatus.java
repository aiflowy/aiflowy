package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

/**
 * 租户启用状态
 */
@DictDef(name = "租户启用状态", code = "tenantStatus", keyField = "code", labelField = "text")
public enum EnumTenantStatus {

    UNAVAILABLE(0, "未启用"),
    AVAILABLE(1, "已启用"),
    ;

    private Integer code;
    private String text;

    EnumTenantStatus(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumTenantStatus getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumTenantStatus type : EnumTenantStatus.values()) {
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
