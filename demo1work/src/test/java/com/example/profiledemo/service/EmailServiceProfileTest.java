package com.example.profiledemo.service;

import com.example.profiledemo.ProfileDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class EmailServiceProfileTest {

    @Test
    void shouldUseDevEmailServiceOnDevProfile() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("dev");
            context.scan(ProfileDemoApplication.class.getPackageName());
            context.refresh();
            EmailService emailService = context.getBean(EmailService.class);
            assertThat(emailService).isInstanceOf(DevEmailService.class);
        }
    }

    @Test
    void shouldUseProdEmailServiceOnProdProfile() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("prod");
            context.scan(ProfileDemoApplication.class.getPackageName());
            context.refresh();
            EmailService emailService = context.getBean(EmailService.class);
            assertThat(emailService).isInstanceOf(ProdEmailService.class);
        }
    }
}
