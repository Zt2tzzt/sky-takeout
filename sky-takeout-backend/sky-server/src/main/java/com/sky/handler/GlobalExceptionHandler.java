package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 此（重载）方法用于：捕获业务异常
     *
     * @param ex 异常
     * @return Result<Object>
     */
    @ExceptionHandler
    public Result<Object> exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());

        return Result.error(ex.getMessage(), null);
    }

    /**
     * 此（重载）方法用于：处理数据库操作异常
     *
     * @param ex 异常
     * @return Result<String>
     */
    @ExceptionHandler
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("异常信息：{}", ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg, null);
        }

        return Result.error(MessageConstant.UNKNOWN_ERROR, null);
    }
}
