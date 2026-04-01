package com.example.demo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 生产环境的数据服务
 */
@Service  // ✅ 加上 @Service 注解
@Profile("prod")  // ✅ 只在 prod 环境生效
public class ProdDataService implements DataService {

    @Override
    public String getData() {
        return "生产环境数据（使用 MySQL 真实数据库）";
    }
}
