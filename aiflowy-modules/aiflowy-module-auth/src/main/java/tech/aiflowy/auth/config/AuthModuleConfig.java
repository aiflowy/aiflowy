package tech.aiflowy.auth.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
public class AuthModuleConfig {

    public AuthModuleConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-auth");
    }
}
