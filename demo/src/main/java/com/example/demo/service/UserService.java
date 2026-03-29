package com.example.demo.service;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    /**
     * BCrypt 密码加密器
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
     * 用户注册（使用 BCrypt 加密密码）
     */
    public void register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        boolean usernameExists = userStore.values().stream()
                .anyMatch(user -> user.getUsername().equals(dto.getUsername()));
        if (usernameExists) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已注册
        boolean emailExists = userStore.values().stream()
                .anyMatch(user -> dto.getEmail().equals(user.getEmail()));
        if (emailExists) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername(dto.getUsername());
        // BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAge(dto.getAge());
        user.setIdCard(dto.getIdCard());
        user.setStatus(1); // 默认正常
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存到"数据库"
        userStore.put(user.getId(), user);
    }

    /**
     * 用户登录（验证密码）
     */
    public User login(LoginDTO dto) {
        User user = userStore.values().stream()
                .filter(u -> u.getUsername().equals(dto.getUsername()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        return user;
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
     * 分组校验-创建用户（Create 分组）
     */
    public void create(UserUpdateDTO dto) {
        boolean usernameExists = userStore.values().stream()
                .anyMatch(user -> user.getUsername().equals(dto.getUsername()));
        if (usernameExists) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userStore.put(user.getId(), user);
    }

    /**
     * 分组校验-更新用户（Update 分组）
     */
    public void update(UserUpdateDTO dto) {
        User user = userStore.get(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
        user.setUpdateTime(LocalDateTime.now());
        userStore.put(user.getId(), user);
    }

    /**
     * 初始化测试数据（密码使用 BCrypt 加密）
     */
    private void initTestData() {

        User user1 = new User();
        user1.setId(idGenerator.getAndIncrement());
        user1.setUsername("zhangsan");
        user1.setPassword(passwordEncoder.encode("Password123"));
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
        user2.setPassword(passwordEncoder.encode("Password123"));
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
        user3.setPassword(passwordEncoder.encode("Password123"));
        user3.setEmail("wangwu@example.com");
        user3.setPhone("13800138002");
        user3.setAge(30);
        user3.setStatus(1);
        user3.setCreateTime(LocalDateTime.now().minusDays(2));
        user3.setUpdateTime(LocalDateTime.now().minusDays(2));
        userStore.put(user3.getId(), user3);
    }
}
