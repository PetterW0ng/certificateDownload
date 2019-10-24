package org.pkucare.exception;

/**
 * 访问受限异常
 * Created by weiqin on 2019/10/24.
 */
public class RequestLimitException extends Exception {

    public RequestLimitException(){
        super("超出了访问次数限制，请过段时间后再试！");
    }

    public RequestLimitException(String message){
        super(message);
    }

}
