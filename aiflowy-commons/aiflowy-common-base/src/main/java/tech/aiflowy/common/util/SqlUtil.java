package tech.aiflowy.common.util;

public class SqlUtil {

    public static String buildOrderBy(String sortKey, String sortType) {
        return buildOrderBy(sortKey, sortType, "");
    }

    public static String buildOrderBy(String sortKey, String sortType, String defaultOrderBy) {
        if (StringUtil.noText(sortKey)) {
            return defaultOrderBy;
        }

        sortKey = sortKey.trim();
        if (StringUtil.noText(sortType)) {
            return sortKey;
        }

        sortType = sortType.toLowerCase().trim();
        if (!"asc".equals(sortType) && !"desc".equals(sortType)) {
            throw new IllegalArgumentException("sortType only support asc or desc");
        }

        com.mybatisflex.core.util.SqlUtil.keepOrderBySqlSafely(sortKey);

        return sortKey + " " + sortType;
    }
}
