package com.example.jpaadvanceddemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建用户请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDTO {

    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status;

    private UserProfileDTO userProfile;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileDTO {
        private String realName;
        private Integer gender;
        private String birthday;  // 使用 String 类型，方便 JSON 序列化
        private String address;
    }
}
