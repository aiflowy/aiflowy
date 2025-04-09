package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;


public class SysOptionBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Column(comment = "租户ID", tenantId = true)
    private BigInteger tenantId;

    /**
     * 配置KEY
     */
    @Id(comment = "配置KEY")
    private String key;

    /**
     * 配置内容
     */
    @Column(comment = "配置内容")
    private String value;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
