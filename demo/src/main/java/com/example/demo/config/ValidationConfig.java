package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.validation.Validator;
import java.util.List;
import java.util.Locale;

/**
 * 参数校验配置
 */
@Configuration
public class ValidationConfig {

    @Autowired
    private MessageSource messageSource;

    /**
     * 配置快速失败模式并注入国际化 MessageSource
     */
    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        // 注入 Spring MessageSource，支持国际化错误消息
        validator.setValidationMessageSource(messageSource);
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

    /**
     * 配置区域解析器，根据请求头 Accept-Language 返回对应语言的错误消息
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.CHINESE);
        resolver.setSupportedLocales(List.of(Locale.CHINESE, Locale.ENGLISH));
        return resolver;
    }
}
