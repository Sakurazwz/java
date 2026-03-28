package com.example.profiledemo.service;

public interface EmailService {
    String send(String to, String subject, String body);
}
