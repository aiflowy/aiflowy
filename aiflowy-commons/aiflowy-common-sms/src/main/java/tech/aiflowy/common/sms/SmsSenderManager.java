package tech.aiflowy.common.sms;

import tech.aiflowy.common.util.SpringContextUtil;

public class SmsSenderManager {


    public static boolean sendSmsCode(String mobile) {
        SmsConfig smsConfig = SmsConfig.getInstance();
        SmsSender smsSender = SpringContextUtil.getBean(smsConfig.getSender(), SmsSender.class);
        String smsCode = SmsUtil.generateSmsCode();
        if (smsSender.sendCode(mobile, smsCode)) {
            SmsCodeCache.put(mobile, smsCode);
            return true;
        } else {
            return false;
        }
    }


    public static boolean isMobileInLimitation(String mobile) {
        return SmsCodeCache.get(mobile) != null;
    }

}
