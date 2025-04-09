package tech.aiflowy.common.filestorage;

import tech.aiflowy.common.util.SpringContextUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aiflowy.storage")
public class StorageConfig {

    //支持 local、minio...
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static StorageConfig getInstance(){
        return SpringContextUtil.getBean(StorageConfig.class);
    }

}
