package tech.aiflowy.system.controller;

import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.tree.Tree;

import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.system.entity.SysMenu;
import tech.aiflowy.system.entity.SysRoleMenu;
import tech.aiflowy.system.service.SysAccountRoleService;
import tech.aiflowy.system.service.SysMenuService;
import tech.aiflowy.system.service.SysRoleMenuService;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单表 控制层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@RestController("sysMenuController")
@RequestMapping("/api/v1/sysMenu")
public class SysMenuController extends BaseCurdController<SysMenuService, SysMenu> {

    @Resource
    private SysRoleMenuService sysRoleMenuService;
    @Resource
    private SysAccountRoleService sysAccountRoleService;

    public SysMenuController(SysMenuService service) {
        super(service);
    }

    @Override
    protected String getDefaultOrderBy() {
        return "sort_no asc";
    }

    @Override
    @GetMapping("list")
    public Result<List<SysMenu>> list(SysMenu entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<SysMenu> sysMenus = service.list(queryWrapper);
        return Result.ok(Tree.tryToTree(sysMenus, "id", "parentId"));
    }

    @GetMapping("tree")
    public Result<List<SysMenu>> tree(SysMenu entity) {
        LoginAccount account = SaTokenUtil.getLoginAccount();
        BigInteger accountId = account.getId();
        List<SysMenu> sysMenus = service.getMenusByAccountId(entity,accountId);
        return Result.ok(Tree.tryToTree(sysMenus, "id", "parentId"));
    }

    /**
     * 根据角色id获取菜单树
     */
    @GetMapping("getCheckedByRoleId/{roleId}")
    public Result getCheckedByRoleId(@PathVariable BigInteger roleId) {
        QueryWrapper rmWrapper = QueryWrapper.create();
        rmWrapper.eq("role_id", roleId);
        List<SysRoleMenu> list = sysRoleMenuService.list(rmWrapper);
        List<BigInteger> menuIds = list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(menuIds)) {
            return Result.success(new ArrayList<>());
        }
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.in(SysMenu::getId,menuIds);
        List<SysMenu> sysMenus = service.list(wrapper);
        return Result.success(sysMenus.stream().map(SysMenu::getId).collect(Collectors.toList()));
    }

    /**
     * 保存后，给超级管理员角色添加权限
     */
    @Override
    protected void onSaveOrUpdateAfter(SysMenu entity, boolean isSave) {
        if (isSave) {
            SysRoleMenu admin = new SysRoleMenu();
            admin.setRoleId(Constants.SUPER_ADMIN_ROLE_ID);
            admin.setMenuId(entity.getId());
            sysRoleMenuService.save(admin);
        }
    }

    /**
     * 删除后，删除角色菜单对应关系
     */
    @Override
    protected void onRemoveAfter(Collection<Serializable> ids) {
        QueryWrapper w = QueryWrapper.create();
        w.in(SysRoleMenu::getMenuId, ids);
        sysRoleMenuService.remove(w);
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysMenu entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return null;
    }
}