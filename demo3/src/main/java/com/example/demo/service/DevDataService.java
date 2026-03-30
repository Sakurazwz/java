package com.example.demo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DevDataService implements DataService {
    @Override
    public String getData() {
        return "开发环境数据（使用 H2 内存数据库）";
    }
}