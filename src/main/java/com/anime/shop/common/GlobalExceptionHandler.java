package com.anime.shop.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 抓所有 Exception */
    @ExceptionHandler(Exception.class)
    public Result<Void> handle(Exception e) {
        log.error("全局异常: ", e);
        return Result.fail(e.getMessage());
    }

    /** 抓自定义业务异常（先写个壳，Day3 补） */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException e) {
        return Result.build(e.getCode(), e.getMessage(), null);
    }
}