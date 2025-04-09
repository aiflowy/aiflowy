package tech.aiflowy.core.dict.loader;

import tech.aiflowy.core.dict.Dict;
import tech.aiflowy.core.dict.DictItem;
import tech.aiflowy.core.dict.DictLoader;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnumDictLoader<E extends Enum<E>> implements DictLoader {

    private final String code;
    private final Dict dict;

    public EnumDictLoader(String code, Class<E> enumClass, String keyField, String labelField) {
        this(null, code, enumClass, keyField, labelField);
    }

    public EnumDictLoader(String name, String code, Class<E> enumClass, String keyField, String labelField) {
        this.code = code;
        E[] enums = enumClass.getEnumConstants();

        this.dict = new Dict();
        this.dict.setName(name);
        this.dict.setCode(code);

        Field keyProperty = ClassUtil.getFirstField(enumClass, field -> field.getName().equals(keyField));
        String keyGetterMethodName = "get" + StringUtil.firstCharToUpperCase(keyField);

        Method keyGetter = ClassUtil.getFirstMethod(enumClass, method -> {
            String methodName = method.getName();
            return methodName.equals(keyGetterMethodName) && Modifier.isPublic(method.getModifiers());
        });

        Field valueProperty = ClassUtil.getFirstField(enumClass, field -> field.getName().equals(keyField));
        String valueGetterMethodName = "get" + StringUtil.firstCharToUpperCase(labelField);

        Method valueGetter = ClassUtil.getFirstMethod(enumClass, method -> {
            String methodName = method.getName();
            return methodName.equals(valueGetterMethodName) && Modifier.isPublic(method.getModifiers());
        });


        List<DictItem> items = new ArrayList<>(enums.length);
        for (E anEnum : enums) {
            Object key = getByMethodOrField(anEnum, keyGetter, keyProperty);
            Object value = getByMethodOrField(anEnum, valueGetter, valueProperty);
            DictItem dictItem = new DictItem();
            dictItem.setValue(key);
            dictItem.setLabel(String.valueOf(value));
            items.add(dictItem);
        }
        this.dict.setItems(items);
    }

    private Object getByMethodOrField(E anEnum, Method keyGetter, Field keyProperty) {
        if (keyGetter != null) {
            try {
                return keyGetter.invoke(anEnum);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return keyProperty.get(anEnum);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public Dict load(String keyword, Map<String, String[]> parameters) {
        return dict;
    }
}
