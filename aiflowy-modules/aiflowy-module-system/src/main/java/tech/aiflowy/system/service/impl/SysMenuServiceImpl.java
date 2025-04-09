package tech.aiflowy.system.service.impl;

import tech.aiflowy.common.util.SqlOperatorsUtil;
import tech.aiflowy.system.entity.*;
import tech.aiflowy.system.mapper.SysMenuMapper;
import tech.aiflowy.system.service.SysAccountRoleService;
import tech.aiflowy.system.service.SysMenuService;
import tech.aiflowy.system.service.SysRoleMenuService;
import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.SqlOperators;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单表 服务层实现。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysRoleMenuService sysRoleMenuService;
    @Resource
    private SysAccountRoleService sysAccountRoleService;

    @Override
    public List<SysMenu> getMenusByAccountId(SysMenu entity, BigInteger accountId) {
        // 查询用户对应角色id集合
        QueryWrapper am = QueryWrapper.create();
        am.eq(SysAccountRole::getAccountId, accountId);
        List<BigInteger> roleIds = sysAccountRoleService.list(am).stream().map(SysAccountRole::getRoleId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        // 查询角色对应的菜单id集合
        QueryWrapper rm = QueryWrapper.create();
        rm.in(SysRoleMenu::getRoleId, roleIds);
        List<BigInteger> menuIds = sysRoleMenuService.list(rm).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(menuIds)) {
            return new ArrayList<>();
        }
        // 查询当前用户拥有的菜单
        SqlOperators ops = SqlOperatorsUtil.build(SysMenu.class);
        QueryWrapper queryWrapper = QueryWrapper.create(entity, ops);
        queryWrapper.in(SysMenu::getId, menuIds);
        queryWrapper.orderBy("sort_no asc");
        return list(queryWrapper);
    }
}
