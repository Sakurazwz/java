package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 只允许前端开发服务器
        config.addAllowedOrigin("http://localhost:5173");

        // 允许携带凭证
        config.setAllowCredentials(true);

        // 允许所有请求方法
        config.addAllowedMethod("*");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 暴露的响应头
        config.addExposedHeader("Content-Disposition");

        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        // 映射路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
