package com.zk.wardrobe.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 响应结果封装类
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    // 成功状态码
    private static final int SUCCESS_CODE = 200;
    // 失败状态码
    private static final int ERROR_CODE = 500;

    protected Result() {}

    protected Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, "操作成功", null);
    }

    /**
     * 成功返回结果（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, "操作成功", data);
    }

    /**
     * 成功返回结果（带提示信息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ERROR_CODE, message, null);
    }

    /**
     * 失败返回结果（自定义错误码）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}