package com.example.demo.controller;

import com.example.demo.config.AppConfig;
import com.example.demo.config.CustomConfig;
import com.example.demo.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "系统接口", description = "系统状态和配置查询接口")
public class HelloController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private DataService dataService;


    @GetMapping("/")
    @Operation(summary = "首页", description = "欢迎页面，显示当前时间")
    public String home() {
        return "欢迎来到 Spring Boot DevTools 测试！当前时间：" + getCurrentTime();
    }

    @GetMapping("/hello")
    @Operation(summary = "简单问候", description = "返回简单的问候语")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    @Operation(summary = "个性化问候", description = "根据名称返回个性化问候")
    public String helloName(
            @Parameter(description = "用户名称", example = "张三")
            @PathVariable String name) {
        return "Hello, " + name + "! 当前时间：" + getCurrentTime();
    }

    /**
     * 获取应用配置信息
     */
    @GetMapping("/config")
    @Operation(summary = "获取配置信息", description = "获取应用配置信息")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("appName", appConfig.getName());
        config.put("version", appConfig.getVersion());
        config.put("author", appConfig.getAuthor());
        config.put("environment", appConfig.getEnvironment());
        config.put("debug", appConfig.getDebug());
        config.put("uploadPath", customConfig.getUploadPath());
        config.put("maxFileSize", customConfig.getMaxFileSize());
        config.put("serverPort", environment.getProperty("server.port"));
        config.put("activeProfile", environment.getProperty("spring.profiles.active"));
        return config;
    }

    /**
     * 获取当前环境信息
     */
    @GetMapping("/env")
    @Operation(summary = "获取环境信息", description = "获取当前环境信息")
    public String getEnv() {
        return String.format("当前环境：%s，端口：%s，调试模式：%s",
                appConfig.getEnvironment(),
                environment.getProperty("server.port"),
                appConfig.getDebug());
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    /**
     * 获取环境相关数据
     */
    @GetMapping("/data")
    @Operation(summary = "获取环境数据", description = "获取环境相关数据")
    public String getData() {
        if (dataService == null) {
            return "当前环境未配置数据服务";
        }
        return dataService.getData();
    }
}
