package com.example.demo.dto;

import com.example.demo.validation.ValidationGroup;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 用户更新 DTO
 */
@Data
public class UserUpdateDTO {

    /**
     * 用户ID（更新时必填）
     */
    @NotNull(message = "{validation.user.id.notnull}", groups = ValidationGroup.Update.class)
    private Long id;

    /**
     * 用户名（新增时必填，更新时可选）
     */
    @NotBlank(message = "{validation.username.notblank}", groups = ValidationGroup.Create.class)
    @Size(min = 3, max = 20, message = "{validation.username.size}", groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
    private String username;

    /**
     * 邮箱（新增时必填，更新时可选）
     */
    @NotBlank(message = "{validation.email.notblank}", groups = ValidationGroup.Create.class)
    @Email(message = "{validation.email.format}", groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
    private String email;

    /**
     * 年龄（两个场景都校验）
     */
    @Min(value = 1, message = "{validation.age.min}")
    @Max(value = 120, message = "{validation.age.max}")
    private Integer age;
}
