package tech.aiflowy.common.web.exceptions;

/**
 * 程序报错
 */
public class ProgramException extends RuntimeException {

    public ProgramException() {
    }

    public ProgramException(String msg) {
        super(msg);
    }
}
