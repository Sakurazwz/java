package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 手机号校验器
 * 使用有界 LRU 缓存存储校验结果，避免对相同输入的重复正则匹配
 * 注：生产环境可将缓存替换为 Redis 以支持分布式场景
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 最大缓存条目数
     */
    private static final int MAX_CACHE_SIZE = 1000;

    /**
     * 有界 LRU 校验结果缓存（生产环境可替换为 Redis）
     */
    private static final Map<String, Boolean> VALIDATION_CACHE = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            });

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;  // null 值交给 @NotNull 处理
        }
        // 优先从缓存中获取校验结果
        return VALIDATION_CACHE.computeIfAbsent(value, v -> PHONE_PATTERN.matcher(v).matches());
    }
}
