package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 密码强度校验器
 */
public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

    private int min;
    private int max;

    /**
     * 初始化方法（获取注解参数）
     */
    @Override
    public void initialize(PasswordStrength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    /**
     * 校验方法
     *
     * @param value   待校验的值
     * @param context 校验上下文
     * @return true-校验通过，false-校验失败
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 值交给 @NotNull 处理
        if (value == null) {
            return true;
        }

        // 长度校验
        if (value.length() < min || value.length() > max) {
            return false;
        }

        // 正则校验：必须包含大小写字母和数字
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        return Pattern.matches(pattern, value);
    }
}
