package com.example.demo.controller;

import com.example.demo.config.AppConfig;
import com.example.demo.config.CustomConfig;
import com.example.demo.service.DataService;
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
public class HelloController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private Environment environment;

    @GetMapping("/")
    public String home() {
        return "欢迎来到 Spring Boot DevTools 测试！当前时间：" + getCurrentTime();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Hello, " + name + "! 当前时间："
                + getCurrentTime();
    }

    /**
     * 获取应用配置信息
     */
    @GetMapping("/config")
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
    public String getEnv() {
        return String.format("当前环境：%s，端口：%s，调试模式：%s",
                appConfig.getEnvironment(),
                environment.getProperty("server.port"),
                appConfig.getDebug());
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Autowired(required = false)
    private DataService dataService;

    @GetMapping("/data")
    public String getData() {
        if (dataService == null) {
            return "当前环境未配置数据服务";
        }
        return dataService.getData();
    }
}