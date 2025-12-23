package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;


public class ModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "ID")
    private BigInteger id;

    /**
     * 部门ID
     */
    @Column(comment = "部门ID")
    private BigInteger deptId;

    /**
     * 租户ID
     */
    @Column(tenantId = true, comment = "租户ID")
    private BigInteger tenantId;

    /**
     * 标题或名称
     */
    @Column(comment = "标题或名称")
    private String title;

    /**
     * 品牌
     */
    @Column(comment = "品牌")
    private String brand;

    /**
     * ICON
     */
    @Column(comment = "ICON")
    private String icon;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * 大模型请求地址
     */
    @Column(comment = "大模型请求地址")
    private String endpoint;

    /**
     * 请求路径
     */
    @Column(comment = "请求路径")
    private String requestPath;

    /**
     * 大模型名称
     */
    @Column(comment = "大模型名称")
    private String modelName;

    /**
     * 大模型 API KEY
     */
    @Column(comment = "大模型 API KEY")
    private String apiKey;

    /**
     * 大模型其他属性配置
     */
    @Column(comment = "大模型其他属性配置")
    private String extraConfig;

    /**
     * 其他配置内容
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "其他配置内容")
    private Map<String, Object> options;

    /**
     * 分组名称
     */
    @Column(comment = "分组名称")
    private String groupName;

    /**
     * 模型类型
     */
    @Column(comment = "模型类型")
    private String modelType;

    /**
     * 供应商
     */
    @Column(comment = "供应商")
    private String provider;

    /**
     * 是否支持推理
     */
    @Column(comment = "是否支持推理")
    private Boolean supportReasoning;

    /**
     * 是否支持工具
     */
    @Column(comment = "是否支持工具")
    private Boolean supportTool;

    /**
     * 是否支持嵌入
     */
    @Column(comment = "是否支持嵌入")
    private Boolean supportEmbedding;

    /**
     * 是否支持重排
     */
    @Column(comment = "是否支持重排")
    private Boolean supportRerank;

    /**
     * 供应商id
     */
    @Column(comment = "供应商id")
    private BigInteger providerId;

    /**
     * 是否添加
     */
    @Column(comment = "是否添加")
    private Boolean added;

    /**
     * 是否能删除
     */
    @Column(comment = "是否能删除")
    private Boolean canDelete;

    /**
     * 是否免费
     */
    @Column(comment = "是否免费")
    private Boolean supportFree;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(String extraConfig) {
        this.extraConfig = extraConfig;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean getSupportReasoning() {
        return supportReasoning;
    }

    public void setSupportReasoning(Boolean supportReasoning) {
        this.supportReasoning = supportReasoning;
    }

    public Boolean getSupportTool() {
        return supportTool;
    }

    public void setSupportTool(Boolean supportTool) {
        this.supportTool = supportTool;
    }

    public Boolean getSupportEmbedding() {
        return supportEmbedding;
    }

    public void setSupportEmbedding(Boolean supportEmbedding) {
        this.supportEmbedding = supportEmbedding;
    }

    public Boolean getSupportRerank() {
        return supportRerank;
    }

    public void setSupportRerank(Boolean supportRerank) {
        this.supportRerank = supportRerank;
    }

    public BigInteger getProviderId() {
        return providerId;
    }

    public void setProviderId(BigInteger providerId) {
        this.providerId = providerId;
    }

    public Boolean getAdded() {
        return added;
    }

    public void setAdded(Boolean added) {
        this.added = added;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Boolean getSupportFree() {
        return supportFree;
    }

    public void setSupportFree(Boolean supportFree) {
        this.supportFree = supportFree;
    }

}
