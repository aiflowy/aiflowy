package tech.aiflowy.system.entity;

import com.mybatisflex.annotation.Column;
import tech.aiflowy.system.entity.base.SysRoleBase;
import com.mybatisflex.annotation.Table;

import java.math.BigInteger;
import java.util.List;

/**
 * 系统角色 实体类。
 *
 * @author ArkLight
 * @since 2025-03-14
 */

@Table(value = "tb_sys_role", comment = "系统角色")
public class SysRole extends SysRoleBase {

    @Column(ignore = true)
    private List<BigInteger> menuIds;
    @Column(ignore = true)
    private List<BigInteger> deptIds;

    public List<BigInteger> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<BigInteger> menuIds) {
        this.menuIds = menuIds;
    }

    public List<BigInteger> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<BigInteger> deptIds) {
        this.deptIds = deptIds;
    }
}
