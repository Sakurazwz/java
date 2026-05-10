package com.example.redisdemo.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 分布式锁组件
 * 基于 setIfAbsent + UUID 实现可重入的安全分布式锁
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLock {

    private final RedisTemplate<String, Object> redisTemplate;

    /** 锁的 key 前缀 */
    private static final String LOCK_PREFIX = "distributed_lock:";

    /** 默认锁超时时间（秒） */
    private static final long DEFAULT_LOCK_TIMEOUT = 10;

    /** 默认获取锁的等待时间（秒） */
    private static final long DEFAULT_WAIT_TIME = 5;

    /**
     * 尝试获取锁
     *
     * @param lockKey  锁的 key
     * @param timeout  锁超时时间
     * @param unit     时间单位
     * @return 锁标识（释放锁时需要），null 表示获取失败
     */
    public String tryLock(String lockKey, long timeout, TimeUnit unit) {
        String lockValue = UUID.randomUUID().toString();
        String key = LOCK_PREFIX + lockKey;

        boolean success = Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, lockValue, timeout, unit)
        );

        if (success) {
            log.debug("获取分布式锁成功: key={}, value={}", key, lockValue);
            return lockValue;
        }

        log.debug("获取分布式锁失败: key={}", key);
        return null;
    }

    /**
     * 阻塞获取锁（默认最多等待 5 秒）
     *
     * @param lockKey 锁的 key
     * @param timeout 锁超时时间
     * @param unit    时间单位
     * @return 锁标识
     */
    public String lock(String lockKey, long timeout, TimeUnit unit) {
        return lock(lockKey, timeout, unit, DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
    }

    /**
     * 阻塞获取锁，指定最大等待时间
     *
     * @param lockKey      锁的 key
     * @param timeout      锁超时时间
     * @param timeoutUnit  锁超时时间单位
     * @param maxWait      最大等待时间
     * @param waitUnit     等待时间单位
     * @return 锁标识，null 表示超时未获取到锁
     */
    public String lock(String lockKey, long timeout, TimeUnit timeoutUnit, long maxWait, TimeUnit waitUnit) {
        long startTime = System.currentTimeMillis();
        long maxWaitMillis = waitUnit.toMillis(maxWait);

        String lockValue;
        while ((lockValue = tryLock(lockKey, timeout, timeoutUnit)) == null) {
            if (System.currentTimeMillis() - startTime > maxWaitMillis) {
                log.warn("获取分布式锁超时: lockKey={}, maxWait={}ms", lockKey, maxWaitMillis);
                return null;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        return lockValue;
    }

    /**
     * 释放锁（使用 Lua 脚本保证原子性：只有持有者才能释放）
     *
     * @param lockKey   锁的 key
     * @param lockValue 锁标识
     * @return 是否释放成功
     */
    public boolean unlock(String lockKey, String lockValue) {
        if (lockValue == null) {
            return false;
        }

        String key = LOCK_PREFIX + lockKey;

        // Lua 脚本：只有 value 匹配时才删除 key，防止误删其他线程的锁
        String script = """
                if redis.call('GET', KEYS[1]) == ARGV[1] then
                    return redis.call('DEL', KEYS[1])
                else
                    return 0
                end""";

        Long result = redisTemplate.execute(
                new org.springframework.data.redis.core.script.DefaultRedisScript<>(script, Long.class),
                java.util.List.of(key),
                lockValue
        );

        boolean success = result != null && result == 1;
        if (success) {
            log.debug("释放分布式锁成功: key={}", key);
        } else {
            log.debug("释放分布式锁失败（锁已过期或不属于当前线程）: key={}", key);
        }
        return success;
    }

    /**
     * 在分布式锁保护下执行操作
     *
     * @param lockKey   锁的 key
     * @param timeout   锁超时时间
     * @param unit      时间单位
     * @param supplier  要执行的操作
     * @return 操作结果，null 表示获取锁失败
     */
    public <T> T executeWithLock(String lockKey, long timeout, TimeUnit unit, Supplier<T> supplier) {
        String lockValue = lock(lockKey, timeout, unit);
        if (lockValue == null) {
            log.warn("未获取到分布式锁，操作被跳过: lockKey={}", lockKey);
            return null;
        }

        try {
            log.info("获取分布式锁成功，执行业务操作: lockKey={}", lockKey);
            return supplier.get();
        } finally {
            unlock(lockKey, lockValue);
        }
    }
}
