package com.example.jpaadvanceddemo.config;

import com.example.jpaadvanceddemo.entity.User;
import com.example.jpaadvanceddemo.entity.UserProfile;
import com.example.jpaadvanceddemo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("开始初始化测试数据...");

        LocalDateTime now = LocalDateTime.now();

        User user1 = new User();
        user1.setUsername("zhangsan");
        user1.setPassword("123456");
        user1.setEmail("zhangsan@example.com");
        user1.setPhone("13800138000");
        user1.setStatus(1);
        user1.setCreateTime(now);
        user1.setUpdateTime(now);

        UserProfile profile1 = new UserProfile();
        profile1.setRealName("张三");
        profile1.setGender(1);
        profile1.setBirthday(LocalDate.parse("1990-01-01"));
        profile1.setAddress("北京市朝阳区");
        profile1.setUser(user1);
        user1.setUserProfile(profile1);

        User user2 = new User();
        user2.setUsername("lisi");
        user2.setPassword("123456");
        user2.setEmail("lisi@example.com");
        user2.setPhone("13800138001");
        user2.setStatus(1);
        user2.setCreateTime(now);
        user2.setUpdateTime(now);

        UserProfile profile2 = new UserProfile();
        profile2.setRealName("李四");
        profile2.setGender(0);
        profile2.setBirthday(LocalDate.parse("1992-05-15"));
        profile2.setAddress("上海市浦东新区");
        profile2.setUser(user2);
        user2.setUserProfile(profile2);

        User user3 = new User();
        user3.setUsername("wangwu");
        user3.setPassword("123456");
        user3.setEmail("wangwu@example.com");
        user3.setPhone("13800138002");
        user3.setStatus(1);
        user3.setCreateTime(now);
        user3.setUpdateTime(now);

        UserProfile profile3 = new UserProfile();
        profile3.setRealName("王五");
        profile3.setGender(1);
        profile3.setBirthday(LocalDate.parse("1988-10-20"));
        profile3.setAddress("广州市天河区");
        profile3.setUser(user3);
        user3.setUserProfile(profile3);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        System.out.println("✓ 创建了3个用户及其详情：");
        System.out.println("  - zhangsan (张三) - 北京市朝阳区");
        System.out.println("  - lisi (李四) - 上海市浦东新区");
        System.out.println("  - wangwu (王五) - 广州市天河区");
    }
}
