package tech.aiflowy.common.entity;

import java.io.Serializable;
import java.math.BigInteger;

public class LoginAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private BigInteger id;

    /**
     * 部门ID
     */
    private BigInteger deptId;

    /**
     * 租户ID
     */
    private BigInteger tenantId;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机电话
     */
    private String mobile;

    /**
     * 邮件
     */
    private String email;

    /**
     * 账户头像
     */
    private String avatar;

    /**
     * 数据权限类型
     */
    private Integer dataScope;

    /**
     * 自定义部门权限
     */
    private String deptIdList;

    /**
     * 备注
     */
    private String remark;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getDeptId() {
        return deptId;
    }

    public void setDeptId(BigInteger deptId) {
        this.deptId = deptId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }

    public String getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(String deptIdList) {
        this.deptIdList = deptIdList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
