package org.pkucare.pojo;

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
        this.data = null;
        this.code = SUCCESS;
        this.message = "success";
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
}
