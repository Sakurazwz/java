package com.example.profiledemo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class ProdEmailService implements EmailService {
    @Override
    public String send(String to, String subject, String body) {
        return String.format("[PROD] 邮件已发送 to=%s, subject=%s", to, subject);
    }
}
