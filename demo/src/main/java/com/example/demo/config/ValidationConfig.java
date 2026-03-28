package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import jakarta.validation.Validator;

/**
 * 参数校验配置
 */
@Configuration
public class ValidationConfig {

    /**
     * 配置快速失败模式（第一个校验失败就返回）
     */
    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        // Hibernate Validator 8.x 使用字符串配置
        validator.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");
        return validator;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }
}
