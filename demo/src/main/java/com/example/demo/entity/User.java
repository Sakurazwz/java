package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码（加密后）
     */
    private String password;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 状态（0-禁用，1-正常）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
