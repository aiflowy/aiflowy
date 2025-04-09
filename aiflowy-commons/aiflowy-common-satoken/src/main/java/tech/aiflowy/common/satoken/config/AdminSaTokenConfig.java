package tech.aiflowy.common.satoken.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 复制此类并自行修改
 */
@Configuration
public class AdminSaTokenConfig {

    public static final String LOGIN_TYPE = "aiflowy";

    @Bean
    @Primary
    public SaTokenConfig setSaTokenConfig() {
        SaTokenConfig config = SaConfigs.defaultConfig();
        config.setTokenName(LOGIN_TYPE + "-token");
        config.setIsConcurrent(true);
        config.setMaxLoginCount(3);
        config.setTimeout(24 * 60 * 60L);
        config.setTokenStyle("simple-uuid");
        StpUtil.stpLogic.setConfig(config);
        return config;
    }
}
