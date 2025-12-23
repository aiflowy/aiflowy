package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;


public class SysRoleMenuBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 角色ID
     */
    @Column(comment = "角色ID")
    private BigInteger roleId;

    /**
     * 菜单ID
     */
    @Column(comment = "菜单ID")
    private BigInteger menuId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(BigInteger roleId) {
        this.roleId = roleId;
    }

    public BigInteger getMenuId() {
        return menuId;
    }

    public void setMenuId(BigInteger menuId) {
        this.menuId = menuId;
    }

}
