package com.example.profiledemo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DevEmailService implements EmailService {
    @Override
    public String send(String to, String subject, String body) {
        String message = String.format("[DEV] 模拟日志邮件 to=%s, subject=%s, body=%s", to, subject, body);
        System.out.println(message);
        return message;
    }
}
