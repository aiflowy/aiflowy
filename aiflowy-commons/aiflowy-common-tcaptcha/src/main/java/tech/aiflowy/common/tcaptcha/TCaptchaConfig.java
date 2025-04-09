package tech.aiflowy.common.tcaptcha;

import tech.aiflowy.common.util.SpringContextUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aiflowy.tcaptcha")
public class TCaptchaConfig {

    private Boolean enable = false;
    private String secretId;
    private String secretKey;
    private Long captchaAppId;
    private String appSecretKey;
    private String[] validPathPatterns = new String[]{"/api/v1/account/login", "/api/v1/account/getcode"};

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Long getCaptchaAppId() {
        return captchaAppId;
    }

    public void setCaptchaAppId(Long captchaAppId) {
        this.captchaAppId = captchaAppId;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public String[] getValidPathPatterns() {
        return validPathPatterns;
    }

    public void setValidPathPatterns(String[] validPathPatterns) {
        this.validPathPatterns = validPathPatterns;
    }

    public static TCaptchaConfig getInstance() {
        return SpringContextUtil.getBean(TCaptchaConfig.class);
    }

}
