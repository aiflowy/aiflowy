package tech.aiflowy.common.options;


import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.common.util.StringUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class SysOptions {

    private final SysOptionStore store;

    public SysOptions(ObjectProvider<SysOptionStore> storeObjectProvider) {
        this.store = storeObjectProvider.getIfAvailable();
    }

    public static void set(String key, String value) {
        getInstance().store.save(key, value);
    }

    public static String get(String key) {
        return getInstance().store.get(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return StringUtil.hasText(value) ? value : defaultValue;
    }

    public static Boolean getAsBoolean(String key) {
        String value = get(key);
        return StringUtil.hasText(value) ? Boolean.parseBoolean(value) : null;
    }


    public static Boolean getAsBoolean(String key, Boolean defaultValue) {
        Boolean value = getAsBoolean(key);
        return value != null ? value : defaultValue;
    }



    private static SysOptions instance = null;

    public static SysOptions getInstance() {
        if (instance == null) {
            instance = SpringContextUtil.getBean(SysOptions.class);
        }
        return instance;
    }


}
