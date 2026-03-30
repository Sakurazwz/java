package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    /**
     * 应用名称
     */
    private
    String name;

    /**
     * 应用版本
     */
    private
    String version;

    /**
     * 作者
     */
    private
    String author;

    /**
     * 环境
     */
    private
    String environment;

    /**
     * 是否调试模式
     */
    private
    Boolean debug;
}