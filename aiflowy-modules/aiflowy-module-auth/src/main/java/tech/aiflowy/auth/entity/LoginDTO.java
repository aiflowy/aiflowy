package tech.aiflowy.auth.entity;

import javax.validation.constraints.NotEmpty;

public class LoginDTO {

    /**
     * 账号
     */
    @NotEmpty(message = "账号不能为空")
    private String account;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
