package tech.aiflowy.system.controller;

import tech.aiflowy.common.constant.enums.EnumAccountType;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.util.StringUtil;

import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.log.annotation.LogRecord;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.service.SysAccountService;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 用户表 控制层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@RestController("sysAccountController")
@RequestMapping("/api/v1/sysAccount")
public class SysAccountController extends BaseCurdController<SysAccountService, SysAccount> {
    public SysAccountController(SysAccountService service) {
        super(service);
    }

    @Override
    @LogRecord("分页查询")
    protected Page<SysAccount> queryPage(Page<SysAccount> page, QueryWrapper queryWrapper) {
        return service.getMapper().paginateWithRelations(page, queryWrapper);
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysAccount entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        BigInteger tenantId = loginUser.getTenantId();
        if (isSave) {
            commonFiled(entity, loginUser.getId(), tenantId, loginUser.getDeptId());
            // 查询用户名是否存在
            // long count = Db.selectCount(SqlPrepare.COUNT_ACCOUNT_BY_UNI_KEY, entity.getLoginName(), tenantId);
            QueryWrapper w = QueryWrapper.create();
            w.eq(SysAccount::getLoginName, entity.getLoginName());
            long count = service.count(w);
            if (count > 0) {
                return Result.fail(1, "用户名已存在");
            }
            String password = entity.getPassword();
            if (StringUtil.hasText(password)) {
                entity.setPassword(BCrypt.hashpw(password));
            }
        } else {
            SysAccount record = service.getById(entity.getId());
            // 如果修改了部门，就将用户踢下线，避免用户操作数据造成数据错误
            if (record.getDeptId() != null && !record.getDeptId().equals(entity.getDeptId())) {
                StpUtil.kickout(record.getId());
            }
            // 不让修改用户名/密码，浏览器记住密码有可能会带上来
            entity.setLoginName(null);
            entity.setPassword(null);
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return null;
    }

    @Override
    protected void onSaveOrUpdateAfter(SysAccount entity, boolean isSave) {
        service.syncRelations(entity);
    }

    @Override
    protected Result onRemoveBefore(Collection<Serializable> ids) {
        List<SysAccount> sysAccounts = service.listByIds(ids);
        for (SysAccount account : sysAccounts) {
            Integer accountType = account.getAccountType();
            if (EnumAccountType.SUPER_ADMIN.getCode().equals(accountType)) {
                return Result.fail(1, "不能删除超级管理员");
            }
            if (EnumAccountType.TENANT_ADMIN.getCode().equals(accountType)) {
                return Result.fail(1, "不能删除租户管理员");
            }
        }
        return super.onRemoveBefore(ids);
    }

    @GetMapping("/myProfile")
    public Result myProfile() {
        LoginAccount account = SaTokenUtil.getLoginAccount();
        SysAccount sysAccount = service.getById(account.getId());
        return Result.success(sysAccount);
    }

    @PostMapping("/updateProfile")
    public Result updateProfile(@JsonBody SysAccount account) {
        BigInteger loginAccountId = SaTokenUtil.getLoginAccount().getId();
        SysAccount update = new SysAccount();
        update.setId(loginAccountId);
        update.setNickname(account.getNickname());
        update.setMobile(account.getMobile());
        update.setEmail(account.getEmail());
        update.setAvatar(account.getAvatar());
        update.setModified(new Date());
        update.setModifiedBy(loginAccountId);
        service.updateById(update);
        return Result.success();
    }

    /**
     * 修改密码，用于修改用户自己的密码
     *
     * @param password        用户的旧密码
     * @param newPassword     新密码
     * @param confirmPassword 确认密码
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(@JsonBody(value = "password", required = true) String password,
                                 @JsonBody(value = "newPassword", required = true) String newPassword,
                                 @JsonBody(value = "confirmPassword", required = true) String confirmPassword) {
        BigInteger loginAccountId = SaTokenUtil.getLoginAccount().getId();
        SysAccount record = service.getById(loginAccountId);
        if (record == null) {
            return Result.fail();
        }
        String pwdDb = record.getPassword();
        if (!BCrypt.checkpw(password, pwdDb)) {
            return Result.fail(1, "密码不正确");
        }
        if (!newPassword.equals(confirmPassword)) {
            return Result.fail(2, "两次密码不一致");
        }
        SysAccount update = new SysAccount();
        update.setId(loginAccountId);
        update.setPassword(BCrypt.hashpw(newPassword));
        update.setModified(new Date());
        update.setModifiedBy(loginAccountId);
        service.updateById(update);
        return Result.success();
    }
}