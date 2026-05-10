package com.example.redisdemo.service;

import com.example.redisdemo.entity.User;
import com.example.redisdemo.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户服务
 * 演示 Redis 缓存注解和高级实战
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLock redisLock;

    /** 库存计数器（模拟共享资源） */
    private final AtomicInteger stock = new AtomicInteger(100);

    // 模拟数据库存储
    private final Map<Long, User> database = new ConcurrentHashMap<>();

    // ==================== 基础缓存（注解方式） ====================

    /**
     * 根据 ID 查询用户
     * 使用 @Cacheable 注解，结果会被缓存
     * unless = "#result == null" - 不缓存 null 值
     */
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        log.info("从数据库查询用户: id={}", id);
        simulateDelay(500);
        return database.get(id);
    }

    /**
     * 创建用户
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

    // ==================== 第五部分：Redis 实战应用 ====================

    /**
     * 1. 自定义缓存过期时间
     * 使用 RedisTemplate 手动操作，设置不同的过期时间
     */
    public User getUserByIdWithCustomTTL(Long id, long timeout, TimeUnit unit) {
        String key = "users::" + id;

        // 先从 Redis 缓存中查询
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            log.info("从缓存查询用户(自定义TTL): id={}", id);
            return user;
        }

        // 缓存未命中，查询数据库
        log.info("从数据库查询用户(自定义TTL): id={}", id);
        simulateDelay(500);
        user = database.get(id);

        // 存入缓存，自定义过期时间
        if (user != null) {
            redisTemplate.opsForValue().set(key, user, timeout, unit);
            log.info("缓存用户(自定义TTL={}{}): id={}", timeout, unit.toString().toLowerCase(), id);
        }

        return user;
    }

    /**
     * 2. 缓存穿透解决方案
     * 问题：查询不存在的数据，每次都穿透缓存查询数据库
     * 解决：缓存空值（设置较短过期时间），防止恶意攻击
     */
    public User getUserByIdWithPenetrationProtection(Long id) {
        String key = "users::" + id;
        String nullKey = "users::null::" + id;

        // 检查是否已经缓存了空值标记
        Object nullMarker = redisTemplate.opsForValue().get(nullKey);
        if (nullMarker != null) {
            log.info("命中空值缓存(防穿透): id={}, 直接返回null", id);
            return null;
        }

        // 从缓存查询
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            log.info("从缓存查询用户(防穿透): id={}", id);
            return user;
        }

        // 查询数据库
        log.info("从数据库查询用户(防穿透): id={}", id);
        simulateDelay(500);
        user = database.get(id);

        if (user != null) {
            // 正常缓存，10分钟过期
            redisTemplate.opsForValue().set(key, user, 10, TimeUnit.MINUTES);
        } else {
            // 缓存空值标记，1分钟过期（防止缓存穿透）
            redisTemplate.opsForValue().set(nullKey, "NULL", 1, TimeUnit.MINUTES);
            log.info("缓存空值标记(防穿透): id={}, 1分钟后过期", id);
        }

        return user;
    }

    /**
     * 3. 缓存雪崩解决方案
     * 问题：大量缓存同时过期，数据库压力骤增
     * 解决：设置随机过期时间（基础时间 + 随机偏移）
     */
    public User getUserByIdWithAvalancheProtection(Long id) {
        String key = "users::" + id;

        User user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            log.info("从缓存查询用户(防雪崩): id={}", id);
            return user;
        }

        // 查询数据库
        log.info("从数据库查询用户(防雪崩): id={}", id);
        simulateDelay(500);
        user = database.get(id);

        if (user != null) {
            // 随机过期时间：基础10分钟 + 随机0~5分钟
            int timeout = 10 + new Random().nextInt(5);
            redisTemplate.opsForValue().set(key, user, timeout, TimeUnit.MINUTES);
            log.info("缓存用户(防雪崩-随机TTL={}分钟): id={}", timeout, id);
        }

        return user;
    }

    /**
     * 4. 缓存击穿解决方案
     * 问题：热点数据过期瞬间，大量请求同时查询数据库
     * 解决：互斥锁（分布式锁），只让一个请求去查数据库，其他请求等待
     */
    public User getUserByIdWithBreakdownProtection(Long id) {
        String key = "users::" + id;
        String lockKey = "lock::users::" + id;

        // 先查缓存
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            log.info("从缓存查询用户(防击穿): id={}", id);
            return user;
        }

        // 缓存未命中，尝试获取分布式锁
        log.info("缓存未命中，尝试获取锁: lockKey={}", lockKey);
        boolean locked = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS)
        );

        if (locked) {
            try {
                // 双重检查：获取锁后再次检查缓存（其他线程可能已经重建了）
                user = (User) redisTemplate.opsForValue().get(key);
                if (user != null) {
                    log.info("双重检查-从缓存查到数据: id={}", id);
                    return user;
                }

                // 查询数据库并重建缓存
                log.info("获取锁成功，重建缓存: id={}", id);
                simulateDelay(500);
                user = database.get(id);

                if (user != null) {
                    redisTemplate.opsForValue().set(key, user, 10, TimeUnit.MINUTES);
                    log.info("缓存重建完成(防击穿): id={}", id);
                }
            } finally {
                // 释放锁
                redisTemplate.delete(lockKey);
                log.info("释放锁: lockKey={}", lockKey);
            }
        } else {
            // 未获取到锁，等待后重试
            log.info("未获取到锁，等待50ms后重试: id={}", id);
            simulateDelay(50);
            // 再次从缓存获取
            user = (User) redisTemplate.opsForValue().get(key);
            if (user != null) {
                log.info("重试成功-从缓存查到数据: id={}", id);
            }
        }

        return user;
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
