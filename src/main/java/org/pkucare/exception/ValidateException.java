package org.pkucare.exception;

/**
 * Created by weiqin on 2019/12/18.
 */
public class ValidateException extends Exception {

    public ValidateException() {
        super("请求参数为空或数据错误，请确认！");
    }

    public ValidateException(String message, Throwable e) {
        super(message, e);
    }

    public ValidateException(String message) {
        super(message);
    }
}
