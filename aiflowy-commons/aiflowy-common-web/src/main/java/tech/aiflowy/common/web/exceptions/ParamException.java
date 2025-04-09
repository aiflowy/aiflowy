package tech.aiflowy.common.web.exceptions;

/**
 * 参数报错
 */
public class ParamException extends RuntimeException {

    public ParamException() {
    }

    public ParamException(String msg) {
        super(msg);
    }
}
