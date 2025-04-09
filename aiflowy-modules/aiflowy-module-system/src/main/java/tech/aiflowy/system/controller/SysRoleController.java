package tech.aiflowy.system.controller;

import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.entity.SysRole;
import tech.aiflowy.system.service.SysRoleService;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 系统角色 控制层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@RestController("sysRoleController")
@RequestMapping("/api/v1/sysRole/")
public class SysRoleController extends BaseCurdController<SysRoleService, SysRole> {
    public SysRoleController(SysRoleService service) {
        super(service);
    }

    @PostMapping("saveRoleMenu/{roleId}")
    public Result saveRoleMenu(@PathVariable("roleId") BigInteger roleId, @JsonBody List<String> keys) {
        service.saveRoleMenu(roleId, keys);
        return Result.success();
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysRole entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
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
                return Result.fail(1,"超级管理员角色不能删除");
            }
            if (Constants.TENANT_ADMIN_ROLE_CODE.equals(roleKey)) {
                return Result.fail(1,"租户管理员角色不能删除");
            }
        }
        return super.onRemoveBefore(ids);
    }
}