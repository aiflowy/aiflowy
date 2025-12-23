package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class SysApiKeyBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "id")
    private BigInteger id;

    /**
     * apiKey
     */
    @Column(comment = "apiKey")
    private String apiKey;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 状态1启用 2失效
     */
    @Column(comment = "状态1启用 2失效")
    private Integer status;

    /**
     * 部门id
     */
    @Column(comment = "部门id")
    private BigInteger deptId;

    /**
     * 租户id
     */
    @Column(tenantId = true, comment = "租户id")
    private BigInteger tenantId;

    /**
     * 失效时间
     */
    @Column(comment = "失效时间")
    private Date expiredAt;

    /**
     * 创建人
     */
    @Column(comment = "创建人")
    private BigInteger createdBy;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    public BigInteger getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy) {
        this.createdBy = createdBy;
    }

}
