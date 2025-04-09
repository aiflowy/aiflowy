package tech.aiflowy.common.sms;

import tech.aiflowy.common.util.TimeoutCacheUtil;

public class SmsCodeCache {
    private static final String cacheName = "smsCode";

    public static void put(String mobile, String value) {
        TimeoutCacheUtil.put(cacheName, mobile, value);
    }

    public static String get(String mobile) {
        return TimeoutCacheUtil.get(cacheName, mobile);
    }

    public static void remove(String mobile) {
        TimeoutCacheUtil.remove(cacheName, mobile);
    }
}
