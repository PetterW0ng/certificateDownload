package org.pkucare.spring;

import com.taobao.api.ApiException;
import org.pkucare.pojo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;

/**
 * 全局异常控制类
 * Created by weiqin on 2019/3/25.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ApiException.class)
    @ResponseBody
    Response handleException(ApiException e) {
        e.printStackTrace();
        Response response = new Response<String>();
        response.setData("短信发送失败了");
        response.setCode(500);
        response.setMessage("短信发送失败了");
        logger.error("后台出错了", e);
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    Response handleException(Exception e) {
        e.printStackTrace();
        Response response = new Response<String>();
        response.setData("后台出错了");
        response.setCode(500);
        response.setMessage("ERROR");
        logger.error("后台出错了", e);
        return response;
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseBody
    Response handleFileNotFoundException(Exception e) {
        e.printStackTrace();
        Response response = new Response<String>();
        response.setData("没有找到文件");
        response.setCode(500);
        response.setMessage("ERROR");
        logger.error("后台出错了", e);
        return response;
    }

}
