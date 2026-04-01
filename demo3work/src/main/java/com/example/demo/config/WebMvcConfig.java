package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 对所有路径生效
                .allowedOrigins("http://localhost:5173")  // 允许前端开发服务器
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的方法
                .allowedHeaders("*")  // 允许的请求头
                .exposedHeaders("Content-Disposition")  // 暴露的响应头
                .allowCredentials(true)  // 允许携带凭证
                .maxAge(3600);  // 预检缓存时间
    }
}
