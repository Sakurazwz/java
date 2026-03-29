package com.example.demo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 开发环境的数据服务
 */
@Service  // ✅ 加上 @Service 注解
@Profile("dev")  // ✅ 只在 dev 环境生效
public class DevDataService implements DataService {

    @Override
    public String getData() {
        return "开发环境数据（使用 H2 内存数据库）";
    }
}