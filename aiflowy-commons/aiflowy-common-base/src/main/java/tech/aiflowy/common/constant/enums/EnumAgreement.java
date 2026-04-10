package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

@DictDef(name = "协议", code = "agreement", keyField = "code", labelField = "text")
public enum EnumAgreement {

    HTTP("http://","http://"),
    HTTPS("https://","https://")
    ;

    private final String code;
    private final String text;

    EnumAgreement(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
