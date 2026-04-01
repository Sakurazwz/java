package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 密码强度校验注解
 * 要求：密码必须包含大小写字母和数字，长度8-20位
 */
@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {

    /**
     * 错误消息
     */
    String message() default "密码必须包含大小写字母和数字，长度8-20位";

    /**
     * 分组（用于分组校验）
     */
    Class<?>[] groups() default {};

    /**
     * 负载（可用于元数据）
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 最小长度
     */
    int min() default 8;

    /**
     * 最大长度
     */
    int max() default 20;
}
