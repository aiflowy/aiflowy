package tech.aiflowy.ai.entity.base;

import tech.aiflowy.common.entity.DateEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;


public class AiKnowledgeBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private BigInteger id;

    /**
     * 别名
     */
    @Column(comment = "别名")
    private String alias;

    /**
     * 部门ID
     */
    @Column(comment = "部门ID")
    private BigInteger deptId;

    /**
     * 租户ID
     */
    @Column(comment = "租户ID", tenantId = true)
    private BigInteger tenantId;

    /**
     * ICON
     */
    @Column(comment = "ICON")
    private String icon;

    /**
     * 标题
     */
    @Column(comment = "标题")
    private String title;

    /**
     * 英文名称
     */
    @Column(comment = "英文名称")
    private String englishName;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * URL 别名
     */
    @Column(comment = "URL 别名")
    private String slug;

    /**
     * 是否启用向量存储
     */
    @Column(comment = "是否启用向量存储")
    private Boolean vectorStoreEnable;

    /**
     * 向量数据库类型
     */
    @Column(comment = "向量数据库类型")
    private String vectorStoreType;

    /**
     * 向量数据库集合
     */
    @Column(comment = "向量数据库集合")
    private String vectorStoreCollection;

    /**
     * 向量数据库配置
     */
    @Column(comment = "向量数据库配置")
    private String vectorStoreConfig;

    /**
     * 是否启用搜索引擎
     */
    @Column(comment = "是否启用搜索引擎")
    private Boolean searchEngineEnable;

    /**
     * Embedding 模型ID
     */
    @Column(comment = "Embedding 模型ID")
    private BigInteger vectorEmbedLlmId;

    /**
     * 重排 模型ID
     */
    @Column(comment = "重排 模型ID")
    private BigInteger rerankLlmId;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建用户ID
     */
    @Column(comment = "创建用户ID")
    private BigInteger createdBy;

    /**
     * 最后一次修改时间
     */
    @Column(comment = "最后一次修改时间")
    private Date modified;

    /**
     * 最后一次修改用户ID
     */
    @Column(comment = "最后一次修改用户ID")
    private BigInteger modifiedBy;

    /**
     * 其他配置
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "其他配置")
    private Map<String, Object> options;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getVectorStoreEnable() {
        return vectorStoreEnable;
    }

    public void setVectorStoreEnable(Boolean vectorStoreEnable) {
        this.vectorStoreEnable = vectorStoreEnable;
    }

    public String getVectorStoreType() {
        return vectorStoreType;
    }

    public void setVectorStoreType(String vectorStoreType) {
        this.vectorStoreType = vectorStoreType;
    }

    public String getVectorStoreCollection() {
        return vectorStoreCollection;
    }

    public void setVectorStoreCollection(String vectorStoreCollection) {
        this.vectorStoreCollection = vectorStoreCollection;
    }

    public String getVectorStoreConfig() {
        return vectorStoreConfig;
    }

    public void setVectorStoreConfig(String vectorStoreConfig) {
        this.vectorStoreConfig = vectorStoreConfig;
    }

    public BigInteger getVectorEmbedLlmId() {return vectorEmbedLlmId;}

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public BigInteger getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(BigInteger createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public BigInteger getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(BigInteger modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public void setVectorEmbedLlmId(BigInteger vectorEmbedLlmId) {this.vectorEmbedLlmId = vectorEmbedLlmId;}

    public BigInteger getRerankLlmId() {return rerankLlmId;}

    public void setRerankLlmId(BigInteger rerankLlmId) {this.rerankLlmId = rerankLlmId;}

    public Boolean getSearchEngineEnable() {
        return searchEngineEnable;
    }

    public void setSearchEngineEnable(Boolean searchEngineEnable) {
        this.searchEngineEnable = searchEngineEnable;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    
}
