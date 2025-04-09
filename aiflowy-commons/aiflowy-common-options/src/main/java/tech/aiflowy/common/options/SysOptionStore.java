package tech.aiflowy.common.options;

public interface SysOptionStore {
     void save(String key,Object value);
     String get(String key);
}
