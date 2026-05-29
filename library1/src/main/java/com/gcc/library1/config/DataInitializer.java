package com.gcc.library1.config;

import com.gcc.library1.model.User;
import com.gcc.library1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动时自动初始化数据
 * 检查是否存在管理员账户，如果不存在则创建默认管理员
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // 检查是否已存在管理员账户（role=ADMIN）
        boolean hasAdmin = userRepository.findAll().stream().anyMatch(user -> "ADMIN".equals(user.getRole()));

        if (!hasAdmin) {
            // 检查指定用户名是否已存在
            if (userRepository.findByName(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setName(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                log.info("已创建默认管理员账户: {}", adminUsername);
            } else {
                // 用户名已存在，将其角色设为管理员
                User existingUser = userRepository.findByName(adminUsername).get();
                existingUser.setRole("ADMIN");
                userRepository.save(existingUser);
                log.info("已将用户 {} 设置为管理员", adminUsername);
            }
        } else {
            log.info("管理员账户已存在，跳过初始化");
        }
    }
}
