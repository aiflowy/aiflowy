package tech.aiflowy.common.sms;


import tech.aiflowy.common.util.SpringContextUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aiflowy.sms")
public class SmsConfig {

    private String sender;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public static SmsConfig getInstance(){
        return SpringContextUtil.getBean(SmsConfig.class);
    }
}
