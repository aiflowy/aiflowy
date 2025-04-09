package tech.aiflowy.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aiflowy.login")
public class LoginProperties {

    private String[] excludes;

    public String[] getExcludes() {
        return excludes;
    }

    public String[] getExcludesOrEmpty() {
        return excludes != null ? excludes : new String[0];
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }
}
