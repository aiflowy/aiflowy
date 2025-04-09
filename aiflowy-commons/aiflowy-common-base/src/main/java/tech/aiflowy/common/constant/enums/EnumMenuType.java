package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

/**
 * 菜单类型
 */
@DictDef(name = "菜单类型", code = "menuType", keyField = "code", labelField = "text")
public enum EnumMenuType {

    MENU(0,"菜单"),
    BTN(1,"按钮");

    private final int code;
    private final String text;

    EnumMenuType(int code, String text) {
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
