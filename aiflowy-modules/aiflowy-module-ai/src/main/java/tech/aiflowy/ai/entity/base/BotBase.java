package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import tech.aiflowy.common.entity.DateEntity;


public class BotBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键ID")
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
    @Column(tenantId = true, comment = "租户ID")
    private BigInteger tenantId;

    /**
     * 标题
     */
    @Column(comment = "标题")
    private String title;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * 图标
     */
    @Column(comment = "图标")
    private String icon;

    /**
     * LLM ID
     */
    @Column(comment = "LLM ID")
    private BigInteger llmId;

    /**
     * LLM选项
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "LLM选项")
    private Map<String, Object> llmOptions;

    /**
     * 选项
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "选项")
    private Map<String, Object> options;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建者ID
     */
    @Column(comment = "创建者ID")
    private BigInteger createdBy;

    /**
     * 修改时间
     */
    @Column(comment = "修改时间")
    private Date modified;

    /**
     * 修改者ID
     */
    @Column(comment = "修改者ID")
    private BigInteger modifiedBy;

    /**
     * 数据状态
     */
    @Column(comment = "数据状态")
    private Boolean status;

    /**
     * 分类ID
     */
    @Column(comment = "分类ID")
    private BigInteger categoryId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public BigInteger getLlmId() {
        return llmId;
    }

    public void setLlmId(BigInteger llmId) {
        this.llmId = llmId;
    }

    public Map<String, Object> getLlmOptions() {
        return llmOptions;
    }

    public void setLlmOptions(Map<String, Object> llmOptions) {
        this.llmOptions = llmOptions;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

}
