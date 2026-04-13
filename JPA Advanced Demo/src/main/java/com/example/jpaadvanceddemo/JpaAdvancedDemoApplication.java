package com.example.jpaadvanceddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class JpaAdvancedDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaAdvancedDemoApplication.class, args);
    }

}
