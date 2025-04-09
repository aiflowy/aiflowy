package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

/**
 * 数据权限类型
 */
@DictDef(name = "数据权限类型", code = "dataScope", keyField = "code", labelField = "text")
public enum EnumDataScope {

    ALL(1, "全部权限"),
    SELF(2, "仅查看本人"),
    DEPT(3, "当前所在部门"),
    DEPT_AND_SUB(4, "当前所在部门及子部门"),
    CUSTOM(5, "自定义权限"),
    ;

    private Integer code;
    private String text;

    EnumDataScope(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumDataScope getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumDataScope type : EnumDataScope.values()) {
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
