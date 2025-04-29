package tech.aiflowy.autoconfig.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan({"tech.aiflowy"})
@org.springframework.boot.autoconfigure.AutoConfiguration
public class AutoConfig {
    public AutoConfig() {
        System.out.println("启用模块 >>>>>>>>>> module-autoconfig");
    }
}
