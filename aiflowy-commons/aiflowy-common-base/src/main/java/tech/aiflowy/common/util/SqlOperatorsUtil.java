package tech.aiflowy.common.util;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.query.SqlOperators;
import com.mybatisflex.core.util.ClassUtil;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlOperatorsUtil {

    private static Map<Class<?>, SqlOperators> sqlOperatorsMap = new ConcurrentHashMap<>();

    public static SqlOperators build(Class<?> entityClass) {
        return new SqlOperators(MapUtil.computeIfAbsent(sqlOperatorsMap, entityClass, aClass -> {
            SqlOperators sqlOperators = new SqlOperators();
            List<Field> allFields = ClassUtil.getAllFields(entityClass);
            allFields.forEach(field -> {
                if (field.getType() == String.class) {
                    Column column = field.getAnnotation(Column.class);
                    if (column != null && column.ignore()) {
                        return;
                    }
                    sqlOperators.set(field.getName(), SqlOperator.LIKE);
                }
            });
            return sqlOperators;
        }));
    }
}
