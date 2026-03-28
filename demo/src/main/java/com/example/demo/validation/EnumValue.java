package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 枚举值校验注解（用于校验字符串是否为枚举值）
 */
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {

    /**
     * 错误消息
     */
    String message() default "值必须在指定的枚举范围内";

    /**
     * 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 枚举类
     */
    Class<? extends Enum<?>> enumClass();
}
