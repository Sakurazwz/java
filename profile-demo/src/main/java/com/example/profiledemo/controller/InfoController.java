package com.example.profiledemo.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class InfoController {

    private final Environment environment;

    public InfoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("environment", currentProfile());
        result.put("port", environment.getProperty("server.port"));
        result.put("applicationName", environment.getProperty("spring.application.name"));
        return result;
    }

    @GetMapping("/switch/{profile}")
    public Map<String, Object> switchProfile(@PathVariable String profile) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("targetProfile", profile);
        result.put("message", "请重启应用并使用 --spring.profiles.active=" + profile + " 以生效");
        result.put("restartRequired", true);
        return result;
    }

    private String currentProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return activeProfiles[0];
        }
        return environment.getProperty("spring.profiles.active", "default");
    }
}
