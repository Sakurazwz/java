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
    @NotNull(message = "用户ID不能为空", groups = ValidationGroup.Update.class)
    private Long id;

    /**
     * 用户名（新增时必填，更新时可选）
     */
    @NotBlank(message = "用户名不能为空", groups = ValidationGroup.Create.class)
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间", groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
    private String username;

    /**
     * 邮箱（新增时必填，更新时可选）
     */
    @NotBlank(message = "邮箱不能为空", groups = ValidationGroup.Create.class)
    @Email(message = "邮箱格式不正确", groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
    private String email;

    /**
     * 年龄（两个场景都校验）
     */
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 120, message = "年龄必须小于120")
    private Integer age;
}
