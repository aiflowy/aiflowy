package tech.aiflowy.common.domain;

import java.util.HashMap;

/**
 * @author michael
 */
public class Result extends HashMap<String, Object> {

    private static final String ATTR_ERROR_CODE = "errorCode";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_DATA = "data";


    private Result(int errorCode) {
        put(ATTR_ERROR_CODE,errorCode);
    }


    public static Result success() {
        return new Result(0);
    }

    public static Result create(boolean success) {
        if (success){
            return success();
        }else {
            return fail(1);
        }
    }

    public static Result success(Object data) {
        Result ret = new Result(0);
        ret.put(ATTR_DATA, data);
        return ret;
    }

    public static Result success(String key, Object value) {
        Result ret = new Result(0);
        ret.put(key, value);
        return ret;
    }

    public static Result fail() {
        return fail(1);
    }

    public static Result fail(int errorCode) {
        return new Result(errorCode);
    }

    public static Result fail(int errorCode, String failMessage) {
        Result result = fail(errorCode);
        result.put(ATTR_MESSAGE, failMessage);
        return result;
    }


    public int failCode() {
        return get(ATTR_ERROR_CODE);
    }

    public void failCode(int errorCode) {
        this.put(ATTR_ERROR_CODE, errorCode);
    }

    public String failMessage() {
        return this.get(ATTR_MESSAGE);
    }

    public void failMessage(String errorMessage) {
        put(ATTR_MESSAGE, errorMessage);
    }

    public Object data() {
        return get(ATTR_DATA);
    }

    public void data(Object data) {
        put(ATTR_DATA, data);
    }

    public <T> T get(String key) {
        return (T) super.get(key);
    }

    public Result set(String key, Object value) {
        put(key, value);
        return this;
    }

    public Result setIfNotNull(String key, Object value) {
        if (value != null && key != null) {
            put(key, value);
        }
        return this;
    }

    public boolean isSuccess() {
        return failCode() == 0;
    }

}
