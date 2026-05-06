package com.example.redisdemo.service;

import com.example.redisdemo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务
 * 演示 Redis 缓存注解的使用
 */
@Slf4j
@Service
public class UserService {

    // 模拟数据库存储
    private final Map<Long, User> database = new ConcurrentHashMap<>();

    /**
     * 根据 ID 查询用户
     * 使用 @Cacheable 注解，结果会被缓存
     * unless = "#result == null" - 不缓存 null 值
     */
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        log.info("从数据库查询用户: id={}", id);

        // 模拟数据库查询延迟
        simulateDelay(500);

        return database.get(id);
    }

    /**
     * 创建用户
     * 使用 @CacheEvict 清空缓存（可选）
     */
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        log.info("创建用户: {}", user.getUsername());

        user.setId(System.currentTimeMillis());
        user.setCreateTime(LocalDateTime.now());

        database.put(user.getId(), user);

        return user;
    }

    /**
     * 更新用户
     * 使用 @CachePut 更新缓存
     */
    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        log.info("更新用户: id={}", user.getId());

        User existing = database.get(user.getId());
        if (existing != null) {
            user.setCreateTime(existing.getCreateTime());
            database.put(user.getId(), user);
        }

        return user;
    }

    /**
     * 删除用户
     * 使用 @CacheEvict 删除缓存
     */
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("删除用户: id={}", id);
        database.remove(id);
    }

    /**
     * 获取所有用户
     */
    public Map<Long, User> getAllUsers() {
        return new HashMap<>(database);
    }

    /**
     * 模拟数据库查询延迟
     */
    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
