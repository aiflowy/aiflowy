package tech.aiflowy.common.sms;

import java.util.concurrent.ThreadLocalRandom;

public class SmsUtil {

    public static String generateSmsCode() {
        StringBuilder value = new StringBuilder();
        value.append(ThreadLocalRandom.current().nextInt(9999));
        while (value.length() < 4) {
            value.insert(0, "0");
        }
        return value.toString();
    }
}
