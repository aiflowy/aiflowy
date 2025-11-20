package tech.aiflowy.system.service.impl;

import tech.aiflowy.common.constant.enums.EnumDataScope;
import tech.aiflowy.system.entity.*;
import tech.aiflowy.system.mapper.SysRoleDeptMapper;
import tech.aiflowy.system.mapper.SysRoleMapper;
import tech.aiflowy.system.mapper.SysRoleMenuMapper;
import tech.aiflowy.system.service.SysAccountRoleService;
import tech.aiflowy.system.service.SysRoleService;
import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色 服务层实现。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysAccountRoleService sysAccountRoleService;
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Resource
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenu(BigInteger roleId, List<String> keys) {
        Db.deleteBySql("delete from tb_sys_role_menu where role_id = ?", roleId);
        List<Row> rows = new ArrayList<>(keys.size());
        SnowFlakeIDKeyGenerator generator = new SnowFlakeIDKeyGenerator();
        keys.forEach(string -> {
            Row row = new Row();
            row.set("id",generator.nextId());
            row.set("role_id", roleId);
            row.set("menu_id", string);
            rows.add(row);
        });
        Db.insertBatch("tb_sys_role_menu", rows);
    }

    @Override
    public List<SysRole> getRolesByAccountId(BigInteger accountId) {
        // 查询用户对应角色id集合
        QueryWrapper am = QueryWrapper.create();
        am.eq(SysAccountRole::getAccountId, accountId);
        List<BigInteger> roleIds = sysAccountRoleService.list(am).stream().map(SysAccountRole::getRoleId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return listByIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(SysRole sysRole) {

        saveOrUpdate(sysRole);

        // 非自定义数据权限，则部门id集合为空
        if (!EnumDataScope.CUSTOM.getCode().equals(sysRole.getDataScope())) {
            sysRole.setDeptIds(new ArrayList<>());
        }

        List<BigInteger> menuIds = sysRole.getMenuIds();
        List<BigInteger> deptIds = sysRole.getDeptIds();

        QueryWrapper dw = QueryWrapper.create();
        dw.eq("role_id", sysRole.getId());
        sysRoleMenuMapper.deleteByQuery(dw);
        sysRoleDeptMapper.deleteByQuery(dw);

        if (CollectionUtil.isNotEmpty(menuIds)) {
            for (BigInteger menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(sysRole.getId());
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }

        if (CollectionUtil.isNotEmpty(deptIds)) {
            for (BigInteger deptId : deptIds) {
                SysRoleDept roleDept = new SysRoleDept();
                roleDept.setRoleId(sysRole.getId());
                roleDept.setDeptId(deptId);
                sysRoleDeptMapper.insert(roleDept);
            }
        }
    }
}
