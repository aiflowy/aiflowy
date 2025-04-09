package tech.aiflowy.core.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
public class CoreModuleConfig {

    public CoreModuleConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-core");
    }
}
