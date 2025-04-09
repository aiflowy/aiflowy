package tech.aiflowy.common.sms;

public interface SmsSender {

    boolean sendCode(String mobile, String code);
}
