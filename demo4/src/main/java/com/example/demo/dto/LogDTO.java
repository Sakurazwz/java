package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 日志实体
 */
@Data
public class LogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作人（用户名或IP）
     */
    private String operator;

    /**
     * 操作方法（类名.方法名）
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 执行时长（毫秒）
     */
    private Long time;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 请求路径
     */
    private String url;

    /**
     * HTTP 方法
     */
    private String httpMethod;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 异常信息
     */
    private String errorMsg;
}
