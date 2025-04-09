package tech.aiflowy.ai.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@MapperScan("tech.aiflowy.ai.mapper")
@AutoConfiguration
public class AiModuleConfig {

    public AiModuleConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-ai");
    }
}
