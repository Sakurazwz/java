package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom")
public class CustomConfig {

    /**
     * 文件上传路径
     */
    private String uploadPath;

    /**
     * 最大文件大小
     */
    private String maxFileSize;
}