package com.example.profiledemo.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class InfoController {

    private static final Set<String> SUPPORTED_PROFILES = Set.of("dev", "test", "prod");
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
        String normalizedProfile = profile == null ? "" : profile.trim().toLowerCase();
        if (!SUPPORTED_PROFILES.contains(normalizedProfile)) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "仅支持切换到 dev、test、prod 环境");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fromProfile", currentProfile());
        result.put("targetProfile", normalizedProfile);
        result.put("supportedProfiles", SUPPORTED_PROFILES);
        result.put("message", "请重启应用并使用 --spring.profiles.active=" + normalizedProfile + " 以生效");
        result.put("restartRequired", true);
        return result;
    }

    private String currentProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            return Arrays.stream(activeProfiles)
                    .filter(SUPPORTED_PROFILES::contains)
                    .findFirst()
                    .orElse(activeProfiles[0]);
        }
        return environment.getProperty("spring.profiles.active", "default");
    }
}
