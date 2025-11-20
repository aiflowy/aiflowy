package tech.aiflowy.system.service;

import tech.aiflowy.system.entity.SysRole;
import com.mybatisflex.core.service.IService;

import java.math.BigInteger;
import java.util.List;

/**
 * 系统角色 服务层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
public interface SysRoleService extends IService<SysRole> {

    void saveRoleMenu(BigInteger roleId, List<String> keys);

    List<SysRole> getRolesByAccountId(BigInteger accountId);

    void saveRole(SysRole sysRole);
}
