package tech.aiflowy.core.dict;

import java.util.Map;

public interface DictLoader {
    String code();
    Dict load(String keyword, Map<String, String[]> parameters);
}
