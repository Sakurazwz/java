package com.example.demo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class ProdDataService implements DataService {
    @Override
    public String getData() {
        return "生产环境数据（使用 MySQL 真实数据库）";
    }
}
