/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.aiflowy.common.web.jsonbody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.util.ConvertUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonBodyParser {

    private static final String startOfArray = "[";
    private static final String endOfArray = "]";


    /**
     * 获取方法里的泛型参数 T 对于的真实的 Class 类
     *
     * @param defClass
     * @param typeVariable
     * @return
     */
    public static Type getTypeVariableRawType(Class<?> defClass, TypeVariable<?> typeVariable) {
        Type type = defClass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArguments.length == 1) {
                return typeArguments[0];
            } else if (typeArguments.length > 1) {
                TypeVariable<?>[] typeVariables = typeVariable.getGenericDeclaration().getTypeParameters();
                for (int i = 0; i < typeVariables.length; i++) {
                    if (typeVariable.getName().equals(typeVariables[i].getName())) {
                        return typeArguments[i];
                    }
                }
            }
        }
        return null;
    }


    public static Object parseJsonBody(Object jsonObjectOrArray, Class<?> paraClass, Type paraType, String jsonKey) throws InstantiationException, IllegalAccessException {
        if (jsonObjectOrArray == null) {
            return paraClass.isPrimitive() ? getPrimitiveDefaultValue(paraClass) : null;
        }
        if (Collection.class.isAssignableFrom(paraClass) || paraClass.isArray()) {
            return parseArray(jsonObjectOrArray, paraClass, paraType, jsonKey);
        } else {
            return parseObject((JSONObject) jsonObjectOrArray, paraClass, paraType, jsonKey);
        }
    }


    public static Object getPrimitiveDefaultValue(Class<?> paraClass) {
        if (paraClass == int.class || paraClass == long.class || paraClass == float.class || paraClass == double.class) {
            return 0;
        } else if (paraClass == boolean.class) {
            return Boolean.FALSE;
        } else if (paraClass == short.class) {
            return (short) 0;
        } else if (paraClass == byte.class) {
            return (byte) 0;
        } else if (paraClass == char.class) {
            return '\u0000';
        } else {
            //不存在这种类型
            return null;
        }
    }


    private static Object parseObject(JSONObject rawObject, Class<?> paraClass, Type paraType, String jsonKey) throws IllegalAccessException, InstantiationException {
        if (!StringUtils.hasText(jsonKey)) {
            return toJavaObject(rawObject, paraClass, paraType);
        }

        Object result = null;
        String[] keys = jsonKey.split("\\.");
        for (int i = 0; i < keys.length; i++) {
            if (rawObject != null && !rawObject.isEmpty()) {
                String key = keys[i].trim();
                if (StringUtils.hasText(key)) {
                    //the last
                    if (i == keys.length - 1) {
                        if (key.endsWith(endOfArray) && key.contains(startOfArray)) {
                            String realKey = key.substring(0, key.indexOf(startOfArray));
                            JSONArray jarray = rawObject.getJSONArray(realKey.trim());
                            if (jarray != null && jarray.size() > 0) {
                                String arrayString = key.substring(key.indexOf(startOfArray) + 1, key.length() - 1);
                                int arrayIndex = StringUtils.hasText(arrayString) ? Integer.parseInt(arrayString.trim()) : 0;
                                result = arrayIndex >= jarray.size() ? null : jarray.get(arrayIndex);
                            }
                        } else {
                            result = rawObject.get(key);
                        }
                    }
                    //not last
                    else {
                        rawObject = getJSONObjectByKey(rawObject, key);
                    }
                }
            }
        }

        if (result == null || "".equals(result)) {
            return paraClass.isPrimitive() ? getPrimitiveDefaultValue(paraClass) : null;
        }

        if (paraClass == String.class && paraClass == paraType) {
            return result.toString();
        }

        // JSONObject 类型
        if (result instanceof JSONObject) {
            return toJavaObject((JSONObject) result, paraClass, paraType);
        }

        return ConvertUtil.convert(result, paraClass);
    }


    private static Object parseArray(Object rawJsonObjectOrArray, Class<?> typeClass, Type type, String jsonKey) {
        JSONArray jsonArray = null;
        if (!StringUtils.hasText(jsonKey)) {
            if (rawJsonObjectOrArray instanceof JSONArray) {
                jsonArray = (JSONArray) rawJsonObjectOrArray;
            }
        } else {
            if (rawJsonObjectOrArray instanceof JSONObject) {
                JSONObject rawObject = (JSONObject) rawJsonObjectOrArray;
                String[] keys = jsonKey.split("\\.");
                for (int i = 0; i < keys.length; i++) {
                    if (rawObject == null || rawObject.isEmpty()) {
                        break;
                    }
                    String key = keys[i].trim();
                    if (StringUtils.hasText(key)) {
                        //the last
                        if (i == keys.length - 1) {
                            if (key.endsWith(endOfArray) && key.contains(startOfArray)) {
                                String realKey = key.substring(0, key.indexOf(startOfArray));
                                JSONArray jarray = rawObject.getJSONArray(realKey.trim());
                                if (jarray == null || jarray.isEmpty()) {
                                    return null;
                                }
                                String subKey = key.substring(key.indexOf(startOfArray) + 1, key.length() - 1).trim();
                                if (!StringUtils.hasText(subKey)) {
                                    throw new IllegalStateException("Sub key can not empty: " + jsonKey);
                                }

                                JSONArray newJsonArray = new JSONArray();
                                for (int j = 0; j < jarray.size(); j++) {
                                    Object value = jarray.getJSONObject(j).get(subKey);
                                    if (value != null) {
                                        newJsonArray.add(value);
                                    }
                                }
                                jsonArray = newJsonArray;
                            } else {
                                jsonArray = rawObject.getJSONArray(key);
                            }
                        }
                        //not last
                        else {
                            rawObject = getJSONObjectByKey(rawObject, key);
                        }
                    }
                }
            }
        }

        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }

        //非泛型 set
        if ((typeClass == Set.class || typeClass == HashSet.class) && typeClass == type) {
            return new HashSet<>(jsonArray);
        }

        //直接获取 JsonArray
        if (typeClass == type && typeClass == JSONArray.class) {
            return jsonArray;
        }

        return jsonArray.toJavaObject(type);
    }


    private static JSONObject getJSONObjectByKey(JSONObject jsonObject, String key) {
        if (key.endsWith(endOfArray) && key.contains(startOfArray)) {
            String realKey = key.substring(0, key.indexOf(startOfArray));
            JSONArray jarray = jsonObject.getJSONArray(realKey.trim());
            if (jarray == null || jarray.isEmpty()) {
                return null;
            }
            String arrayString = key.substring(key.indexOf(startOfArray) + 1, key.length() - 1);
            int arrayIndex = StringUtils.hasText(arrayString) ? Integer.parseInt(arrayString.trim()) : 0;
            return arrayIndex >= jarray.size() ? null : jarray.getJSONObject(arrayIndex);
        } else {
            return jsonObject.getJSONObject(key);
        }
    }

    private static Object toJavaObject(JSONObject rawObject, Class<?> paraClass, Type paraType) throws IllegalAccessException, InstantiationException {
        if (rawObject.isEmpty()) {
            return paraClass.isPrimitive() ? getPrimitiveDefaultValue(paraClass) : null;
        }

        //非泛型 的 map
        if ((paraClass == Map.class || paraClass == JSONObject.class) && paraClass == paraType) {
            return rawObject;
        }

        //非泛型 的 map
        if (Map.class.isAssignableFrom(paraClass) && paraClass == paraType && canNewInstance(paraClass)) {
            Map map = (Map) paraClass.newInstance();
            map.putAll(rawObject);
            return map;
        }

        return rawObject.toJavaObject(paraType);
    }


    private static boolean canNewInstance(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        return !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers);
    }

}