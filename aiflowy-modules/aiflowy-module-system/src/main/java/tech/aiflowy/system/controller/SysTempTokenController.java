package tech.aiflowy.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/temp-token")
public class SysTempTokenController {

    @GetMapping("/create")
    @SaIgnore
    public Result<String> createTempToken() {

        StpUtil.login(0);
        String tokenValue = StpUtil.getTokenValue();
        LoginAccount loginAccount = new LoginAccount();
        loginAccount.setId(BigInteger.valueOf(0));
        loginAccount.setLoginName("匿名用户");
        StpUtil.getSession().set(Constants.LOGIN_USER_KEY, loginAccount);

        return Result.ok("", tokenValue);
    }
}
