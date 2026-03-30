package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置 API 文档基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 文档基本信息
                .info(new Info()
                        .title("Spring Boot API 文档")
                        .description("基于 Spring Boot 3.5.10 + SpringDoc 2.8.0 的 API 文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("你的名字")
                                .email("your-email@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                // JWT 认证配置（可选）
                // 如果项目使用 JWT 认证，取消注释下面的代码
//                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
//                .components(new Components()
//                        .addSecuritySchemes("bearer-key",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")))
                ;
    }
}
