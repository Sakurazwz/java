package com.example.profiledemo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseConfigTest {

    @Autowired
    private DatabaseConfig databaseConfig;

    @Test
    void shouldBindDatasourceProperties() {
        assertThat(databaseConfig.getUrl()).isEqualTo("jdbc:mysql://localhost:3306/profile_demo");
        assertThat(databaseConfig.getUsername()).isEqualTo("root");
        assertThat(databaseConfig.getPassword()).isEqualTo("root");
    }
}
