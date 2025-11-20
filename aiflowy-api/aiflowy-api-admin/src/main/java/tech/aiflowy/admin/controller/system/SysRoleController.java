package tech.aiflowy.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.entity.SysRole;
import tech.aiflowy.system.entity.SysRoleDept;
import tech.aiflowy.system.entity.SysRoleMenu;
import tech.aiflowy.system.service.SysRoleDeptService;
import tech.aiflowy.system.service.SysRoleMenuService;
import tech.aiflowy.system.service.SysRoleService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色 控制层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@RestController("sysRoleController")
@RequestMapping("/api/v1/sysRole/")
public class SysRoleController extends BaseCurdController<SysRoleService, SysRole> {

    @Resource
    private SysRoleMenuService sysRoleMenuService;
    @Resource
    private SysRoleDeptService sysRoleDeptService;

    public SysRoleController(SysRoleService service) {
        super(service);
    }

    @PostMapping("saveRoleMenu/{roleId}")
    @SaCheckPermission("/api/v1/sysRole/save")
    @Deprecated
    public Result<Void> saveRoleMenu(@PathVariable("roleId") BigInteger roleId, @JsonBody List<String> keys) {
        service.saveRoleMenu(roleId, keys);
        return Result.ok();
    }

    /**
     * 获取角色菜单id
     */
    @GetMapping("/getRoleMenuIds")
    @SaCheckPermission("/api/v1/sysRole/query")
    public Result<List<BigInteger>> getRoleMenuIds(BigInteger roleId) {
        QueryWrapper w = QueryWrapper.create();
        w.eq("role_id", roleId);
        List<BigInteger> res = sysRoleMenuService.list(w).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        return Result.ok(res);
    }

    /**
     * 获取角色部门id
     */
    @GetMapping("/getRoleDeptIds")
    @SaCheckPermission("/api/v1/sysRole/query")
    public Result<List<BigInteger>> getRoleDeptIds(BigInteger roleId) {
        QueryWrapper w = QueryWrapper.create();
        w.eq("role_id", roleId);
        List<BigInteger> res = sysRoleDeptService.list(w).stream().map(SysRoleDept::getDeptId).collect(Collectors.toList());
        return Result.ok(res);
    }

    /**
     * 保存角色
     */
    @PostMapping("saveRole")
    @SaCheckPermission("/api/v1/sysRole/save")
    public Result<Void> saveRole(@JsonBody SysRole entity) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (entity.getId() == null) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), loginUser.getDeptId());
        }
        service.saveRole(entity);
        return Result.ok();
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysRole entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return null;
    }

    @Override
    protected Result onRemoveBefore(Collection<Serializable> ids) {
        List<SysRole> sysRoles = service.listByIds(ids);
        for (SysRole sysRole : sysRoles) {
            String roleKey = sysRole.getRoleKey();
            if (Constants.SUPER_ADMIN_ROLE_CODE.equals(roleKey)) {
                return Result.fail(1, "超级管理员角色不能删除");
            }
            if (Constants.TENANT_ADMIN_ROLE_CODE.equals(roleKey)) {
                return Result.fail(1, "租户管理员角色不能删除");
            }
        }
        return super.onRemoveBefore(ids);
    }
}