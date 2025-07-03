package tech.aiflowy.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import tech.aiflowy.auth.entity.LoginDTO;
import tech.aiflowy.auth.entity.LoginVO;
import tech.aiflowy.auth.service.AuthService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("login")
    public Result login(@JsonBody LoginDTO loginDTO) {
        LoginVO res = authService.login(loginDTO);
        return Result.success(res);
    }

    @PostMapping("logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    @GetMapping("getPermissions")
    public Result getPermissions() {
        List<String> permissionList = StpUtil.getPermissionList();
        return Result.success(permissionList);
    }
}
