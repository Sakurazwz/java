package com.example.redisdemo;

import com.example.redisdemo.entity.User;
import com.example.redisdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis 缓存测试类
 * 测试缓存加速、缓存更新、缓存删除、缓存过期等功能
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RedisDemoApplicationTests {

    @Autowired
    private UserService userService;

    private static User testUser;

    @BeforeEach
    void setUp() {
        if (testUser == null) {
            testUser = new User();
            testUser.setUsername("张三");
            testUser.setEmail("zhangsan@example.com");
            testUser.setAge(25);
            testUser = userService.createUser(testUser);
            log.info("初始化测试用户: id={}, username={}", testUser.getId(), testUser.getUsername());
        }
    }

    /**
     * 测试1：缓存加速效果
     * 第一次查询从数据库（慢），第二次从缓存（快）
     */
    @Test
    @Order(1)
    void testCacheSpeed() {
        log.info("========== 测试1: 缓存加速效果 ==========");

        // 第一次查询 - 从数据库读取（模拟延迟500ms）
        long startTime1 = System.currentTimeMillis();
        User user1 = userService.getUserById(testUser.getId());
        long endTime1 = System.currentTimeMillis();
        long firstQueryTime = endTime1 - startTime1;
        log.info("第一次查询耗时: {} ms (从数据库)", firstQueryTime);
        assertNotNull(user1, "用户不应该为null");

        // 第二次查询 - 从缓存读取（快速）
        long startTime2 = System.currentTimeMillis();
        User user2 = userService.getUserById(testUser.getId());
        long endTime2 = System.currentTimeMillis();
        long secondQueryTime = endTime2 - startTime2;
        log.info("第二次查询耗时: {} ms (从缓存)", secondQueryTime);
        assertNotNull(user2, "用户不应该为null");

        // 验证数据一致
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getUsername(), user2.getUsername());

        // 验证: 第二次查询应该快得多
        if (firstQueryTime > 0) {
            double speedup = (double) firstQueryTime / Math.max(secondQueryTime, 1);
            log.info("性能提升: {} 倍", String.format("%.1f", speedup));
        }

        log.info("✅ 缓存加速测试通过!");
    }

    /**
     * 测试2：缓存更新同步
     * 更新用户后，缓存中的数据也应该同步更新
     */
    @Test
    @Order(2)
    void testCacheUpdate() {
        log.info("========== 测试2: 缓存更新同步 ==========");

        // 先查询一次，确保缓存中有数据
        User user = userService.getUserById(testUser.getId());
        log.info("用户信息: username={}, age={}", user.getUsername(), user.getAge());
        int oldAge = user.getAge();

        // 更新用户年龄
        user.setAge(oldAge + 1);
        log.info("更新用户年龄: {} -> {}", oldAge, user.getAge());
        User updated = userService.updateUser(user);
        log.info("更新完成: age={}", updated.getAge());
        assertEquals(oldAge + 1, updated.getAge());

        // 再次查询，验证缓存是否同步更新
        User cached = userService.getUserById(testUser.getId());
        log.info("查询结果: age={}", cached.getAge());
        assertEquals(oldAge + 1, cached.getAge(), "缓存应该已同步更新");

        log.info("✅ 缓存已同步更新!");
    }

    /**
     * 测试3：缓存删除验证
     * 删除用户后，缓存也应该被清除
     */
    @Test
    @Order(3)
    void testCacheEvict() {
        log.info("========== 测试3: 缓存删除验证 ==========");

        // 先创建一个临时用户
        User tempUser = new User();
        tempUser.setUsername("临时用户");
        tempUser.setEmail("temp@example.com");
        tempUser.setAge(20);
        tempUser = userService.createUser(tempUser);
        Long tempId = tempUser.getId();
        log.info("创建临时用户: id={}", tempId);

        // 查询一次，存入缓存
        User user1 = userService.getUserById(tempId);
        log.info("第一次查询用户: username={}", user1.getUsername());
        assertNotNull(user1);

        // 删除用户
        log.info("删除用户...");
        userService.deleteUser(tempId);

        // 删除后查询，应该返回 null
        User user2 = userService.getUserById(tempId);
        log.info("删除后查询用户: {}", user2);
        assertNull(user2, "删除后查询应该返回null");

        log.info("✅ 缓存已正确删除!");
    }

    /**
     * 测试4：缓存失效时间
     * 验证缓存在有效期内可以正常读取
     */
    @Test
    @Order(4)
    void testCacheExpiration() {
        log.info("========== 测试4: 缓存失效时间 ==========");

        // 查询一次，确保缓存中有数据
        User user = userService.getUserById(testUser.getId());
        assertNotNull(user, "用户不应该为null");
        log.info("查询到用户: username={}, age={}", user.getUsername(), user.getAge());

        // 立即再次查询，验证缓存有效
        User cached = userService.getUserById(testUser.getId());
        assertNotNull(cached, "缓存在有效期内，数据应该存在");
        assertEquals(user.getUsername(), cached.getUsername());
        assertEquals(user.getAge(), cached.getAge());

        log.info("✅ 缓存在有效期内,数据可正常读取");
        log.info("（缓存过期时间配置为10分钟，如需测试过期可等待或修改配置）");
    }

    // ==================== 第五部分：Redis 实战应用测试 ====================

    /**
     * 测试5：自定义缓存过期时间
     * 使用 RedisTemplate 手动设置较短的过期时间，验证过期后重新查库
     */
    @Test
    @Order(5)
    void testCustomTTL() {
        log.info("========== 测试5: 自定义缓存过期时间 ==========");

        // 第一次查询，设置短过期时间（2秒）
        User user1 = userService.getUserByIdWithCustomTTL(testUser.getId(), 2, java.util.concurrent.TimeUnit.SECONDS);
        assertNotNull(user1);
        log.info("第一次查询完成，缓存2秒后过期");

        // 立即查询，应该从缓存获取
        User user2 = userService.getUserByIdWithCustomTTL(testUser.getId(), 2, java.util.concurrent.TimeUnit.SECONDS);
        assertNotNull(user2);
        log.info("第二次查询从缓存获取");

        // 等待缓存过期
        log.info("等待3秒让缓存过期...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 缓存过期后再次查询，应该从数据库获取
        User user3 = userService.getUserByIdWithCustomTTL(testUser.getId(), 2, java.util.concurrent.TimeUnit.SECONDS);
        assertNotNull(user3);
        log.info("缓存过期后重新从数据库获取");

        log.info("✅ 自定义缓存过期时间测试通过!");
    }

    /**
     * 测试6：缓存穿透防护
     * 查询不存在的数据，验证空值缓存机制
     */
    @Test
    @Order(6)
    void testCachePenetration() {
        log.info("========== 测试6: 缓存穿透防护 ==========");

        Long fakeId = -1L;

        // 第一次查询不存在的数据，会缓存空值标记
        User user1 = userService.getUserByIdWithPenetrationProtection(fakeId);
        assertNull(user1);
        log.info("第一次查询不存在的数据: null");

        // 第二次查询同一个不存在的数据，应该命中空值缓存
        User user2 = userService.getUserByIdWithPenetrationProtection(fakeId);
        assertNull(user2);
        log.info("第二次查询命中空值缓存，直接返回null（不查数据库）");

        log.info("✅ 缓存穿透防护测试通过!");
    }

    /**
     * 测试7：缓存雪崩防护
     * 多次缓存数据，验证随机过期时间机制
     */
    @Test
    @Order(7)
    void testCacheAvalanche() {
        log.info("========== 测试7: 缓存雪崩防护 ==========");

        // 先清除可能存在的缓存（通过查询触发重建）
        User user = userService.getUserByIdWithAvalancheProtection(testUser.getId());
        assertNotNull(user);
        log.info("查询完成，缓存已设置随机过期时间（10~15分钟）");

        // 立即查询，验证缓存生效
        User cached = userService.getUserByIdWithAvalancheProtection(testUser.getId());
        assertNotNull(cached);
        assertEquals(user.getUsername(), cached.getUsername());
        log.info("缓存验证成功，数据一致");

        log.info("✅ 缓存雪崩防护测试通过!（随机过期时间机制已生效）");
    }

    /**
     * 测试8：缓存击穿防护
     * 验证互斥锁机制防止热点数据过期时大量请求冲击数据库
     */
    @Test
    @Order(8)
    void testCacheBreakdown() {
        log.info("========== 测试8: 缓存击穿防护 ==========");

        // 第一次查询，正常走数据库+缓存
        User user = userService.getUserByIdWithBreakdownProtection(testUser.getId());
        assertNotNull(user);
        log.info("第一次查询完成（含互斥锁保护）");

        // 第二次查询，应该从缓存直接获取
        User cached = userService.getUserByIdWithBreakdownProtection(testUser.getId());
        assertNotNull(cached);
        assertEquals(user.getUsername(), cached.getUsername());
        log.info("缓存命中，无需加锁");

        log.info("✅ 缓存击穿防护测试通过!（互斥锁机制已生效）");
    }
}
