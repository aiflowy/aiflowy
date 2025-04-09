package tech.aiflowy.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Properties;

public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    public static Properties textToProperties(String text) {
        Properties prop = new Properties();
        try (StringReader reader = new StringReader(text)) {
            prop.load(reader);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return prop;
    }


    /**
     * 将Properties对象转换为指定类型的实体对象。
     *
     * @param properties  包含配置信息的Properties对象
     * @param entityClass 目标实体类的Class对象
     * @param <T>         目标实体类的泛型类型
     * @return 转换后的实体对象
     */
    public static <T> T propertiesToEntity(Properties properties, Class<T> entityClass) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            for (Field field : entityClass.getDeclaredFields()) {
                String fieldName = field.getName();
                String propertyValue = properties.getProperty(fieldName);

                if (propertyValue != null) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    if (fieldType.equals(String.class)) {
                        field.set(entity, propertyValue);
                    } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                        field.set(entity, Integer.parseInt(propertyValue));
                    } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                        field.set(entity, Long.parseLong(propertyValue));
                    } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                        field.set(entity, Boolean.parseBoolean(propertyValue));
                    } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                        field.set(entity, Double.parseDouble(propertyValue));
                    } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                        field.set(entity, Float.parseFloat(propertyValue));
                    } else {
                        // 处理其他类型，例如自定义对象
                        // 这里可以根据需要扩展
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert properties to entity", e);
        }
    }

    public static <T> T propertiesTextToEntity(String propertiesText, Class<T> entityClass) {
        Properties properties = textToProperties(propertiesText);
        return propertiesToEntity(properties, entityClass);
    }
}
