package org.pkucare.pojo;

import org.pkucare.pojo.constant.ResultCode;

/**
 * Created by weiqin on 2019/6/14.
 */
public class Response<T> {

    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;

    private T data;

    private int code;

    private String message;

    public Response() {
        this(ResultCode.SUCCESS);
    }

    public Response(ResultCode resultCode) {
        this.data = null;
        this.code = resultCode.code;
        this.message = resultCode.message;
    }

    public void setResultCode(ResultCode resultCode){
        this.code = resultCode.code;
        this.message = resultCode.message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Response SUCCESS(){
        return new Response(ResultCode.SUCCESS);
    }

    public static Response ERROR(){
        return new Response(ResultCode.ERROR);
    }
}
