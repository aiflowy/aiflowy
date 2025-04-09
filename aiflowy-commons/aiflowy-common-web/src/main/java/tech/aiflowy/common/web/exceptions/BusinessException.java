package tech.aiflowy.common.web.exceptions;

/**
 * 业务报错
 */
public class BusinessException extends RuntimeException {

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }
}
