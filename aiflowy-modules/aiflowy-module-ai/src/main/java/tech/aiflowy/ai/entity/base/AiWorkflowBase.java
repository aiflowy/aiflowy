package tech.aiflowy.ai.entity.base;

import tech.aiflowy.common.entity.DateEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class AiWorkflowBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "ID 主键")
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
     * ICON
     */
    @Column(comment = "ICON")
    private String icon;

    /**
     * 工作流设计的 JSON 内容
     */
    @Column(comment = "工作流设计的 JSON 内容")
    private String content;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建人
     */
    @Column(comment = "创建人")
    private BigInteger createdBy;

    /**
     * 最后修改时间
     */
    @Column(comment = "最后修改时间")
    private Date modified;

    /**
     * 最后修改的人
     */
    @Column(comment = "最后修改的人")
    private BigInteger modifiedBy;

    /**
     * 数据状态
     */
    @Column(comment = "数据状态")
    private Integer status;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
