package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import tech.aiflowy.common.entity.DateEntity;


public class SysDictItemBase extends DateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 归属哪个字典
     */
    @Column(comment = "归属哪个字典")
    private BigInteger dictId;

    /**
     * 名称或内容
     */
    @Column(comment = "名称或内容")
    private String text;

    /**
     * 值
     */
    @Column(comment = "值")
    private String value;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;

    /**
     * 排序
     */
    @Column(comment = "排序")
    private Integer sortNo;

    /**
     * css样式内容
     */
    @Column(comment = "css样式内容")
    private String cssContent;

    /**
     * css样式类名
     */
    @Column(comment = "css样式类名")
    private String cssClass;

    /**
     * 备注
     */
    @Column(comment = "备注")
    private String remark;

    /**
     * 状态
     */
    @Column(comment = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 修改时间
     */
    @Column(comment = "修改时间")
    private Date modified;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getDictId() {
        return dictId;
    }

    public void setDictId(BigInteger dictId) {
        this.dictId = dictId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public String getCssContent() {
        return cssContent;
    }

    public void setCssContent(String cssContent) {
        this.cssContent = cssContent;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}
