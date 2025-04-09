package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

/**
 * 用户类型
 */
@DictDef(name = "用户类型", code = "accountType", keyField = "code", labelField = "text")
public enum EnumAccountType {

    NORMAL(0, "普通账号"),
    TENANT_ADMIN(1, "租户管理员"),
    SUPER_ADMIN(99, "超级管理员"),
    ;

    private Integer code;
    private String text;

    EnumAccountType(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumAccountType getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumAccountType type : EnumAccountType.values()) {
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
