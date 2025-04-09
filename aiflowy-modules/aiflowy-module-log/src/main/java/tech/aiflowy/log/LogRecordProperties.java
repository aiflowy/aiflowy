package tech.aiflowy.log;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aiflowy.log-record")
public class LogRecordProperties {

    private String recordActionPrefix;

    public String getRecordActionPrefix() {
        return recordActionPrefix;
    }

    public void setRecordActionPrefix(String recordActionPrefix) {
        this.recordActionPrefix = recordActionPrefix;
    }
}
