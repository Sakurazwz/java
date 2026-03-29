package com.example.demo.dto;

import com.example.demo.enums.Gender;
import com.example.demo.validation.IdCard;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户注册 DTO
 */
@Data
@Schema(description = "用户注册请求参数")
public class UserRegisterDTO {

    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3, maxLength = 20)
    @NotBlank(message = "{validation.username.notblank}")
    @Size(min = 3, max = 20, message = "{validation.username.size}")
    private String username;

    @Schema(description = "密码", example = "Password123", requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$")
    @NotBlank(message = "{validation.password.notblank}")
    @Size(min = 8, max = 20, message = "{validation.password.size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$",
            message = "{validation.password.pattern}")
    private String password;

    @Schema(description = "确认密码", example = "Password123")
    @NotBlank(message = "{validation.confirmPassword.notblank}")
    private String confirmPassword;

    @Schema(description = "邮箱", example = "zhangsan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{validation.email.notblank}")
    @Email(message = "{validation.email.format}")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "{validation.phone.pattern}")
    private String phone;

    @Schema(description = "年龄", example = "20")
    @Min(value = 1, message = "{validation.age.min}")
    @Max(value = 120, message = "{validation.age.max}")
    private Integer age;

    @Schema(description = "性别", example = "MALE")
    private Gender gender;

    @Schema(description = "身份证号", example = "110101199003071234")
    @IdCard
    private String idCard;
}
