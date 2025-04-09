package tech.aiflowy.system.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("tech.aiflowy.system.mapper")
@AutoConfiguration
public class SysModuleConfig {

    public SysModuleConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-system");
    }
}
