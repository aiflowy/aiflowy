package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class PluginBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 插件id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "插件id")
    private BigInteger id;

    /**
     * 别名
     */
    @Column(comment = "别名")
    private String alias;

    /**
     * 名称
     */
    @Column(comment = "名称")
    private String name;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * 类型
     */
    @Column(comment = "类型")
    private Integer type;

    /**
     * 基础URL
     */
    @Column(comment = "基础URL")
    private String baseUrl;

    /**
     * 认证方式  【apiKey/none】
     */
    @Column(comment = "认证方式  【apiKey/none】")
    private String authType;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 图标地址
     */
    @Column(comment = "图标地址")
    private String icon;

    /**
     * 认证参数位置 【headers, query】
     */
    @Column(comment = "认证参数位置 【headers, query】")
    private String position;

    /**
     * 请求头
     */
    @Column(comment = "请求头")
    private String headers;

    /**
     * token键
     */
    @Column(comment = "token键")
    private String tokenKey;

    /**
     * token值
     */
    @Column(comment = "token值")
    private String tokenValue;

    /**
     * 部门id
     */
    @Column(comment = "部门id")
    private Long deptId;

    /**
     * 租户id
     */
    @Column(tenantId = true, comment = "租户id")
    private Long tenantId;

    /**
     * 创建人
     */
    @Column(comment = "创建人")
    private Long createdBy;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

}
