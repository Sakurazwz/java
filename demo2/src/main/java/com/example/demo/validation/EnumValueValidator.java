package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 枚举值校验器
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private Class<? extends Enum<?>> enumClass;

    /**
     * 初始化方法（获取注解参数）
     */
    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    /**
     * 校验方法
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 值交给 @NotNull 处理
        if (value == null || value.isEmpty()) {
            return true;
        }

        // 遍历枚举常量，检查是否匹配枚举名称
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value)) {
                return true;  // 匹配成功
            }
        }

        return false;  // 不匹配
    }
}
