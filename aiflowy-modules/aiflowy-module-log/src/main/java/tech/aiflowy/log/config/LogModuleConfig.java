package tech.aiflowy.log.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@MapperScan("tech.aiflowy.log.mapper")
@AutoConfiguration
public class LogModuleConfig {

    public LogModuleConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-log");
    }
}
