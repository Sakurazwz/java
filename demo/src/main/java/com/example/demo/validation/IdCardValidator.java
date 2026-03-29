package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 身份证号校验器（校验18位身份证号格式）
 */
public class IdCardValidator implements ConstraintValidator<IdCard, String> {

    /**
     * 18位身份证号正则：
     * - 前6位：地区码（数字）
     * - 中间8位：出生日期（YYYYMMDD）
     * - 后3位：顺序码（数字）
     * - 最后1位：校验码（数字或X）
     */
    private static final Pattern ID_CARD_PATTERN =
            Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

    /**
     * 加权因子
     */
    private static final int[] WEIGHT_FACTORS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 校验码映射
     */
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // null 值交给 @NotNull 处理
        }
        if (!ID_CARD_PATTERN.matcher(value).matches()) {
            return false;
        }
        return isValidCheckCode(value);
    }

    /**
     * 验证身份证校验码
     */
    private boolean isValidCheckCode(String idCard) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * WEIGHT_FACTORS[i];
        }
        char expectedCheckCode = CHECK_CODES[sum % 11];
        char actualCheckCode = Character.toUpperCase(idCard.charAt(17));
        return expectedCheckCode == actualCheckCode;
    }
}
