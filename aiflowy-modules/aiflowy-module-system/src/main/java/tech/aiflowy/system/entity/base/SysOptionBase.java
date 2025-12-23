package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import java.io.Serializable;
import java.math.BigInteger;


public class SysOptionBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Column(tenantId = true, comment = "租户ID")
    private BigInteger tenantId;

    /**
     * 配置KEY
     */
    @Column(comment = "配置KEY")
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
