package tech.aiflowy.system.entity;

import tech.aiflowy.system.entity.base.SysAccountRoleBase;
import com.mybatisflex.annotation.Table;

/**
 * 用户-角色表 实体类。
 *
 * @author ArkLight
 * @since 2025-03-14
 */

@Table(value = "tb_sys_account_role", comment = "用户-角色表")
public class SysAccountRole extends SysAccountRoleBase {
}
