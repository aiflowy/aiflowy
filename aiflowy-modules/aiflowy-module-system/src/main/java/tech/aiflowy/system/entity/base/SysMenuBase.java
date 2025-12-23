package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import tech.aiflowy.common.entity.DateTreeEntity;


public class SysMenuBase extends DateTreeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 父菜单id
     */
    @Column(comment = "父菜单id")
    private BigInteger parentId;

    /**
     * 菜单类型
     */
    @Column(comment = "菜单类型")
    private Integer menuType;

    /**
     * 菜单标题
     */
    @Column(comment = "菜单标题")
    private String menuTitle;

    /**
     * 菜单url
     */
    @Column(comment = "菜单url")
    private String menuUrl;

    /**
     * 组件路径
     */
    @Column(comment = "组件路径")
    private String component;

    /**
     * 图标/图片地址
     */
    @Column(comment = "图标/图片地址")
    private String menuIcon;

    /**
     * 是否显示
     */
    @Column(comment = "是否显示")
    private Integer isShow;

    /**
     * 权限标识
     */
    @Column(comment = "权限标识")
    private String permissionTag;

    /**
     * 排序
     */
    @Column(comment = "排序")
    private Integer sortNo;

    /**
     * 数据状态
     */
    @Column(comment = "数据状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 创建者
     */
    @Column(comment = "创建者")
    private BigInteger createdBy;

    /**
     * 修改时间
     */
    @Column(comment = "修改时间")
    private Date modified;

    /**
     * 修改者
     */
    @Column(comment = "修改者")
    private BigInteger modifiedBy;

    /**
     * 备注
     */
    @Column(comment = "备注")
    private String remark;

    /**
     * 删除标识
     */
    @Column(isLogicDelete = true, comment = "删除标识")
    private Integer isDeleted;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public String getPermissionTag() {
        return permissionTag;
    }

    public void setPermissionTag(String permissionTag) {
        this.permissionTag = permissionTag;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}
