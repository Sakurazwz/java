package com.example.profiledemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProfileDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldUseDevAsDefaultProfile() throws IOException {
        assertThat(readResource("application.yaml")).contains("active: dev");
    }

    @Test
    void shouldProvideDevEnvironmentConfig() throws IOException {
        String content = readResource("application-dev.yaml");
        assertThat(content).contains("name: profile-demo-dev");
        assertThat(content).contains("port: 8080");
    }

    @Test
    void shouldProvideTestEnvironmentConfig() throws IOException {
        String content = readResource("application-test.yaml");
        assertThat(content).contains("name: profile-demo-test");
        assertThat(content).contains("port: 8081");
    }

    @Test
    void shouldProvideProdEnvironmentConfig() throws IOException {
        String content = readResource("application-prod.yaml");
        assertThat(content).contains("name: profile-demo-prod");
        assertThat(content).contains("port: 8082");
    }

    private String readResource(String fileName) throws IOException {
        return new String(new ClassPathResource(fileName).getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

}
