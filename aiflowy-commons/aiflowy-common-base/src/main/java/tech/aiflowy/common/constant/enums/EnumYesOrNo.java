package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "是否", code = "yesOrNo", keyField = "code", labelField = "text")
public enum EnumYesOrNo {

    YES(1,"是"),
    NO(0,"否");

    private final int code;
    private final String text;

    EnumYesOrNo(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
