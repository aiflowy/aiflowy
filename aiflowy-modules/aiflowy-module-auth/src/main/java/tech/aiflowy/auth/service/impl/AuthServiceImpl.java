package tech.aiflowy.auth.service.impl;

import tech.aiflowy.auth.entity.LoginDTO;
import tech.aiflowy.auth.entity.LoginVO;
import tech.aiflowy.auth.service.AuthService;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.constant.enums.EnumDataStatus;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.entity.SysMenu;
import tech.aiflowy.system.entity.SysRole;
import tech.aiflowy.system.service.SysAccountService;
import tech.aiflowy.system.service.SysMenuService;
import tech.aiflowy.system.service.SysRoleService;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService, StpInterface {

    @Resource
    private SysAccountService sysAccountService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysMenuService sysMenuService;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        LoginVO res = new LoginVO();
        try {
            TenantManager.ignoreTenantCondition();
            String pwd = loginDTO.getPassword();
            QueryWrapper w = QueryWrapper.create();
            w.eq(SysAccount::getLoginName, loginDTO.getAccount());
            SysAccount record = sysAccountService.getOne(w);
            if (record == null) {
                throw new BusinessException("用户名/密码错误");
            }
            if (EnumDataStatus.UNAVAILABLE.getCode().equals(record.getStatus())) {
                throw new BusinessException("账号未启用，请联系管理员");
            }
            String pwdDb = record.getPassword();
            if (!BCrypt.checkpw(pwd, pwdDb)) {
                throw new BusinessException("用户名/密码错误");
            }
            StpUtil.login(record.getId());
            LoginAccount loginAccount = new LoginAccount();
            BeanUtil.copyProperties(record, loginAccount);
            StpUtil.getSession().set(Constants.LOGIN_USER_KEY, loginAccount);
            String tokenValue = StpUtil.getTokenValue();
            res.setToken(tokenValue);
            res.setNickname(record.getNickname());
            res.setAvatar(record.getAvatar());
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return res;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<SysMenu> menus = sysMenuService.getMenusByAccountId(new SysMenu(), BigInteger.valueOf(Long.parseLong(loginId.toString())));
        return menus.stream()
                .map(SysMenu::getPermissionTag)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<SysRole> roles = sysRoleService.getRolesByAccountId(BigInteger.valueOf(Long.parseLong(loginId.toString())));
        return roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
    }
}
