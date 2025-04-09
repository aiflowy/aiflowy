package tech.aiflowy.auth.service;

import tech.aiflowy.auth.entity.LoginDTO;
import tech.aiflowy.auth.entity.LoginVO;

public interface AuthService {
    /**
     * 登录
     */
    LoginVO login(LoginDTO loginDTO);
}
