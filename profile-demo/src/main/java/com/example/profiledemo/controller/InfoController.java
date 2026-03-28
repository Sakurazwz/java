package com.example.profiledemo.controller;

import com.example.profiledemo.config.AppConfig;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping
public class InfoController {

    private final ConfigurableEnvironment environment;
    private final AppConfig appConfig;
    private final ContextRefresher contextRefresher;

    public InfoController(ConfigurableEnvironment environment,
                          AppConfig appConfig,
                          ContextRefresher contextRefresher) {
        this.environment = environment;
        this.appConfig = appConfig;
        this.contextRefresher = contextRefresher;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        result.put("appName", environment.getProperty("spring.application.name", "unknown"));
        result.put("port", environment.getProperty("local.server.port",
                environment.getProperty("server.port", "unknown")));

        // 业务配置（可热刷新）
        result.put("envName", appConfig.getEnvName());
        result.put("featureFlag", appConfig.getFeatureFlag());
        result.put("welcome", appConfig.getWelcome());
        return result;
    }

    @GetMapping("/switch/{profile}")
    public Map<String, Object> switchProfile(@PathVariable String profile) {
        Set<String> allowed = Set.of("dev", "test", "prod");
        Map<String, Object> result = new LinkedHashMap<>();

        if (!allowed.contains(profile)) {
            result.put("success", false);
            result.put("message", "不支持的环境：" + profile + "，仅支持 dev/test/prod");
            return result;
        }

        // 仅切“业务配置语义”：改 activeProfiles + refresh
        environment.setActiveProfiles(profile);
        Set<String> keys = contextRefresher.refresh();

        result.put("success", true);
        result.put("activeProfilesNow", Arrays.asList(environment.getActiveProfiles()));
        result.put("refreshedKeysCount", keys.size());
        result.put("refreshedKeys", keys);
        result.put("message", "已尝试热刷新业务配置（端口不会变化）");
        return result;
    }

    @PostMapping("/refresh-now")
    public Map<String, Object> refreshNow() {
        Set<String> keys = contextRefresher.refresh();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("refreshedKeysCount", keys.size());
        result.put("refreshedKeys", keys);
        return result;
    }
}
