package tech.aiflowy.core.dict;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "字典类型", code = "dictType", keyField = "value", labelField = "text")
public enum DictType {
    CUSTOM(1, "自定义字典"),
    TABLE(2, "数据表字典"),
    ENUM(3, "枚举类字典"),
    SYSTEM(4, "系统字典"),
    ;


    private final int value;
    private final String text;

    DictType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
