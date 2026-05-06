package com.zk.wardrobe.handler;

import com.zk.wardrobe.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有的 RuntimeException 业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        // 打印错误日志，方便后端排查
        log.error("运行时异常：", e);
        // 统一包装为 Result.error 返回给前端
        return Result.error(e.getMessage());
    }

    /**
     * 捕获其他未知异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("未知异常：", e);
        return Result.error("系统繁忙，请稍后再试");
    }
}