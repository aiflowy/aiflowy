package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class SysLogBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "ID")
    private BigInteger id;

    /**
     * 操作人
     */
    @Column(comment = "操作人")
    private BigInteger accountId;

    /**
     * 操作名称
     */
    @Column(comment = "操作名称")
    private String actionName;

    /**
     * 操作的类型
     */
    @Column(comment = "操作的类型")
    private String actionType;

    /**
     * 操作涉及的类
     */
    @Column(comment = "操作涉及的类")
    private String actionClass;

    /**
     * 操作涉及的方法
     */
    @Column(comment = "操作涉及的方法")
    private String actionMethod;

    /**
     * 操作涉及的 URL 地址
     */
    @Column(comment = "操作涉及的 URL 地址")
    private String actionUrl;

    /**
     * 操作涉及的用户 IP 地址
     */
    @Column(comment = "操作涉及的用户 IP 地址")
    private String actionIp;

    /**
     * 操作请求参数
     */
    @Column(comment = "操作请求参数")
    private String actionParams;

    /**
     * 操作请求body
     */
    @Column(comment = "操作请求body")
    private String actionBody;

    /**
     * 操作状态 1 成功 9 失败
     */
    @Column(comment = "操作状态 1 成功 9 失败")
    private Integer status;

    /**
     * 操作时间
     */
    @Column(comment = "操作时间")
    private Date created;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getAccountId() {
        return accountId;
    }

    public void setAccountId(BigInteger accountId) {
        this.accountId = accountId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionClass() {
        return actionClass;
    }

    public void setActionClass(String actionClass) {
        this.actionClass = actionClass;
    }

    public String getActionMethod() {
        return actionMethod;
    }

    public void setActionMethod(String actionMethod) {
        this.actionMethod = actionMethod;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getActionIp() {
        return actionIp;
    }

    public void setActionIp(String actionIp) {
        this.actionIp = actionIp;
    }

    public String getActionParams() {
        return actionParams;
    }

    public void setActionParams(String actionParams) {
        this.actionParams = actionParams;
    }

    public String getActionBody() {
        return actionBody;
    }

    public void setActionBody(String actionBody) {
        this.actionBody = actionBody;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
