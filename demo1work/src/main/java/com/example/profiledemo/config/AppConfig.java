package com.example.profiledemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String envName;
    private Boolean featureFlag;
    private String welcome;

    public String getEnvName() { return envName; }
    public void setEnvName(String envName) { this.envName = envName; }

    public Boolean getFeatureFlag() { return featureFlag; }
    public void setFeatureFlag(Boolean featureFlag) { this.featureFlag = featureFlag; }

    public String getWelcome() { return welcome; }
    public void setWelcome(String welcome) { this.welcome = welcome; }
}
