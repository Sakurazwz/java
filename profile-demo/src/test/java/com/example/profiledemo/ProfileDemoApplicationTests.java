package com.example.profiledemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProfileDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldUseDevAsDefaultProfile() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(ProfileDemoApplication.class)
                .properties("spring.main.web-application-type=none")
                .run()) {
            Environment environment = context.getEnvironment();
            assertThat(environment.getActiveProfiles()).contains("dev");
            assertThat(environment.getProperty("spring.application.name")).isEqualTo("profile-demo-dev");
            assertThat(environment.getProperty("server.port", Integer.class)).isEqualTo(8080);
        }
    }

    @Test
    void shouldProvideDevEnvironmentConfig() {
        assertProfileConfiguration("dev", "profile-demo-dev", 8080);
    }

    @Test
    void shouldProvideTestEnvironmentConfig() {
        assertProfileConfiguration("test", "profile-demo-test", 8081);
    }

    @Test
    void shouldProvideProdEnvironmentConfig() {
        assertProfileConfiguration("prod", "profile-demo-prod", 8082);
    }

    private void assertProfileConfiguration(String profile, String expectedName, int expectedPort) {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(ProfileDemoApplication.class)
                .profiles(profile)
                .properties("spring.main.web-application-type=none")
                .run()) {
            Environment environment = context.getEnvironment();
            assertThat(environment.getActiveProfiles()).contains(profile);
            assertThat(environment.getProperty("spring.application.name")).isEqualTo(expectedName);
            assertThat(environment.getProperty("server.port", Integer.class)).isEqualTo(expectedPort);
        }
    }

}
