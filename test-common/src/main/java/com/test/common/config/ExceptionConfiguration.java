package com.test.common.config;

import com.test.common.entity.BaseResult;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class ExceptionConfiguration {

    //异常处理的方法
    @ExceptionHandler({ConstraintViolationException.class, BindException.class})
    public BaseResult exceptionHandler(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        String msg = "";
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;
            ConstraintViolation<?> next = constraintViolationException.getConstraintViolations().iterator().next();
            msg = next.getMessage();
        } else if (ex instanceof BindException) {
            BindException bindException = (BindException) ex;
            msg = bindException.getBindingResult().getFieldError().getDefaultMessage();
        }
        return BaseResult.fail(msg);
    }

    /**
     * 处理valid注解不匹配异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public BaseResult processValidationException(ValidationException e) {
        return BaseResult.fail(e.getMessage());
    }
}