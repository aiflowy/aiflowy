package tech.aiflowy.common.satoken.config;

import cn.dev33.satoken.config.SaTokenConfig;

public class SaConfigs {

    public static SaTokenConfig defaultConfig() {
        SaTokenConfig config = new SaTokenConfig();
        config.setTokenName("sa-token");
        config.setTimeout(2592000L);
        // 是否允许同一账号并发登录 （为 true 时允许一起登录，为 false 时新登录挤掉旧登录）
        config.setIsConcurrent(false);
        // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
        config.setIsShare(false);
        // 是否尝试从 请求体 里读取 Token
        config.setIsReadBody(false);
        // 是否尝试从 cookie 里读取 Token，此值为 false 后，StpUtil.login(id) 登录时也不会再往前端注入Cookie
        config.setIsReadCookie(false);
        config.setIsPrint(false);
        config.setIsLog(false);
        return config;
    }
}
