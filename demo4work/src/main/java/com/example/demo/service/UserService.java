package com.example.demo.service;

import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务（内存存储，用于演示 API 文档）
 * 注意：第5周会替换为 MyBatis-Plus + 数据库实现
 */
@Service
public class UserService {

    /**
     * 模拟数据库存储
     */
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();

    /**
     * ID 生成器
     */
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // 初始化一些测试数据
        initTestData();
    }

    /**
     * 根据ID查询用户
     */
    public User getById(Long id) {
        return userStore.get(id);
    }

    /**
     * 查询所有用户
     */
    public List<User> list() {
        return new ArrayList<>(userStore.values());
    }

    /**
     * 用户注册
     */
    public void register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        userStore.values().forEach(user -> {
            if (user.getUsername().equals(dto.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
        });

        // 创建新用户
        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // 实际应该加密
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAge(dto.getAge());
        user.setStatus(1); // 默认正常
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存到"数据库"
        userStore.put(user.getId(), user);
    }

    /**
     * 批量保存
     */
    public void saveBatch(List<User> users) {
        users.forEach(user -> {
            if (user.getId() == null) {
                user.setId(idGenerator.getAndIncrement());
            }
            if (user.getCreateTime() == null) {
                user.setCreateTime(LocalDateTime.now());
            }
            user.setUpdateTime(LocalDateTime.now());
            userStore.put(user.getId(), user);
        });
    }

    /**
     * 初始化测试数据
     */
    private void initTestData() {
        User user1 = new User();
        user1.setId(idGenerator.getAndIncrement());
        user1.setUsername("zhangsan");
        user1.setPassword("Password123");
        user1.setEmail("zhangsan@example.com");
        user1.setPhone("13800138000");
        user1.setAge(20);
        user1.setStatus(1);
        user1.setCreateTime(LocalDateTime.now().minusDays(10));
        user1.setUpdateTime(LocalDateTime.now().minusDays(10));
        userStore.put(user1.getId(), user1);

        User user2 = new User();
        user2.setId(idGenerator.getAndIncrement());
        user2.setUsername("lisi");
        user2.setPassword("Password123");
        user2.setEmail("lisi@example.com");
        user2.setPhone("13800138001");
        user2.setAge(25);
        user2.setStatus(1);
        user2.setCreateTime(LocalDateTime.now().minusDays(5));
        user2.setUpdateTime(LocalDateTime.now().minusDays(5));
        userStore.put(user2.getId(), user2);

        User user3 = new User();
        user3.setId(idGenerator.getAndIncrement());
        user3.setUsername("wangwu");
        user3.setPassword("Password123");
        user3.setEmail("wangwu@example.com");
        user3.setPhone("13800138002");
        user3.setAge(30);
        user3.setStatus(1);
        user3.setCreateTime(LocalDateTime.now().minusDays(2));
        user3.setUpdateTime(LocalDateTime.now().minusDays(2));
        userStore.put(user3.getId(), user3);
    }
}
