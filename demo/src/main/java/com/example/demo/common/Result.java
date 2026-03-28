package com.example.demo.common;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 请求ID（用于链路追踪）
     */
    private String traceId;

    public Result() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Result(int code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(ResultCode resultCode, T data) {
        this();
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS, null);
    }

    /**
     * 成功返回（有数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    /**
     * 成功返回（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>(ResultCode.SUCCESS, data);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败返回（使用错误码枚举）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode, null);
    }

    /**
     * 失败返回（自定义错误码和消息）
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回（自定义错误码枚举和数据）
     */
    public static <T> Result<T> error(ResultCode resultCode, T data) {
        return new Result<>(resultCode, data);
    }

    /**
     * 失败返回（仅消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.BAD_REQUEST.getCode(), message, null);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
