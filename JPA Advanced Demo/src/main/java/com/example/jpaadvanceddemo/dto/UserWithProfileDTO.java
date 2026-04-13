package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户 DTO（包含用户详情）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithProfileDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private String createTime;
    private String updateTime;
    private UserProfile userProfile;
}
