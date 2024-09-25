package com.bond.allianz.global;

import com.bond.allianz.Dao.logs;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value=Exception.class)
    public void defaultErrorHandler(HttpServletRequest request,Exception ex){
        Map map = new HashMap();
        map.put("code", 0);
        map.put("message", ex.getMessage());
        map.put("url", request.getRequestURL());
        map.put("params", request.getParameterMap());
        logs.error("发生未处理的异常,"+request.getRequestURL()+","+ex.getMessage(),ex);
    }

}
