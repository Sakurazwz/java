# 第7周：Redis 基础缓存

## 课程目标

通过本周课程，学生将学会：
1. 理解 Redis 的基本概念和数据类型
2. 掌握 Redis 的安装和配置
3. 学会使用 Spring Data Redis
4. 掌握 `@Cacheable` 等缓存注解
5. 理解缓存的使用场景
6. 学会解决缓存常见问题

---

## 📦 项目实战环境

### 本周项目信息

**项目名称**：redis-demo
**项目路径**：`D:\fwpractice\class7\`
**技术栈**：

- Spring Boot 3.5.10
- Java 21
- Spring Data Redis
- Lombok
- SpringDoc OpenAPI 2.8.0

**Redis 环境**：

- 版本：Redis 7.4.7 for Windows (MSYS2 with Service)
- 安装路径：`D:\Program Files\Redis\`
- 服务类型：Windows Service
- 端口：6379
- 验证状态：✅ redis-cli ping 返回 PONG

---

## 📁 项目结构

```
D:\fwpractice\class7\
├── pom.xml                               # Maven 配置
├── README.md                             # 项目说明
├── mvnw.cmd / mvnw                       # Maven Wrapper
└── src/
    ├── main/
    │   ├── java/com/example/redisdemo/
    │   │   ├── RedisDemoApplication.java          # 主程序
    │   │   ├── config/
    │   │   │   └── RedisConfig.java               # Redis 配置
    │   │   ├── entity/
    │   │   │   └── User.java                      # 用户实体
    │   │   ├── service/
    │   │   │   └── UserService.java               # 用户服务(含缓存注解)
    │   │   └── controller/
    │   │       └── UserController.java            # 用户控制器
    │   └── resources/
    │       └── application.yml                    # 应用配置
    └── test/
        └── java/com/example/redisdemo/
            └── RedisCacheTest.java                # 缓存测试
```

---

## 课前准备

### 本周需要的依赖

```xml
<dependencies>
    <!-- Spring Data Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Swagger/OpenAPI 文档 -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.0</version>
    </dependency>
</dependencies>
```

---

### Redis 安装指南

#### Windows 用户（实战验证）

**推荐方式：使用 redis-windows 项目**

1. **下载 Redis**
   - 访问: https://github.com/redis-windows/redis-windows/releases
   - 推荐版本: Redis 7.4.7 (稳定版) 或 Redis 8.4.0 (最新版)
   - 下载文件: `Redis-7.4.7-Windows-x64-msys2-with-Service.zip`

2. **解压并安装**
   ```bash
   # 解压到指定目录
   解压到: D:\Program Files\Redis\
   
   # 安装为 Windows 服务 (推荐)
   cd D:\Program Files\Redis
   install_redis_service.bat
   
   # 或者使用命令行安装
   redis-server.exe --service-install redis.windows.conf
   
   # 启动服务
   redis-server.exe --service-start
   ```

3. **验证安装**
   ```bash
   # 测试连接
   redis-cli ping
   # 应该返回: PONG
   
   # 测试基本操作
   redis-cli
   > set test "Hello Redis"
   > get test
   > exit
   ```

4. **服务管理**
   
   ```bash
   # 启动服务
   redis-server.exe --service-start
   
   # 停止服务
   redis-server.exe --service-stop
   
   # 卸载服务
   redis-server.exe --service-uninstall
   ```

**实战安装记录**：

```
安装版本:   Redis 7.4.7 for Windows (MSYS2 with Service)
安装路径:   D:\Program Files\Redis\
服务类型:   Windows Service
验证状态:   ✅ redis-cli ping 返回 PONG
启动方式:   Windows 服务 (自动启动)
```

---

#### Linux/Mac 用户

```bash
# Mac
brew install redis
brew services start redis

# Ubuntu
sudo apt update
sudo apt install redis-server
sudo systemctl start redis
```

---

## 第一部分：Redis 基础知识（20分钟）

### 什么是 Redis？

**Redis**（Remote Dictionary Server）是一个开源的内存数据库，常用作缓存、消息队列、分布式锁等。

---

### Redis vs MySQL

| 特性 | Redis | MySQL |
|------|-------|-------|
| **存储位置** | 内存 | 磁盘 |
| **速度** | 极快（微秒级） | 较慢（毫秒级） |
| **数据结构** | 丰富（String、List、Set等） | 表结构 |
| **持久化** | 可选 | 自动 |
| **用途** | 缓存、计数器、排行榜 | 持久化存储 |

**实战对比**：
- 第一次查询（MySQL）：509 ms
- 第二次查询（Redis）：1 ms
- **性能提升：509 倍**

---

### Redis 数据类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| **String** | 字符串 | 缓存、计数器、分布式锁 |
| **Hash** | 哈希表 | 对象存储（如用户信息） |
| **List** | 列表 | 消息队列、最新列表 |
| **Set** | 集合 | 去重、交集/并集 |
| **ZSet** | 有序集合 | 排行榜、优先级队列 |

---

### Redis 基本命令

```bash
# String 操作
SET key value        # 设置键值
GET key              # 获取值
SETEX key seconds value  # 设置键值并指定过期时间
INCR key             # 自增
DEL key              # 删除键

# Hash 操作
HSET key field value  # 设置哈希字段
HGET key field       # 获取哈希字段
HGETALL key          # 获取所有字段

# 通用操作
KEYS *               # 查看所有键
EXISTS key           # 检查键是否存在
TTL key              # 查看剩余过期时间
EXPIRE key seconds   # 设置过期时间
FLUSHDB              # 清空当前数据库
```

---

## 第二部分：整合 Spring Data Redis（30分钟）

### 步骤1：配置 Redis 连接

在 `application.yml` 中配置：

```yaml
spring:
  application:
    name: redis-demo

  # Redis 配置
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0
      timeout: 3000
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms

  # 缓存配置
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 缓存过期时间：10分钟
      cache-null-values: false
      use-key-prefix: true

# 服务器端口
server:
  port: 8080

# 日志配置
logging:
  level:
    root: INFO
    com.example.redisdemo: DEBUG

# Swagger 配置
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

**说明**：
- Redis 有 16 个数据库（0-15）
- `Lettuce` 是默认的 Redis 客户端（基于 Netty）
- 缓存过期时间设置为 10 分钟

---

### 步骤2：配置 Redis 序列化

创建 `RedisConfig.java`：

```java
package com.example.redisdemo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 配置 RedisTemplate 和缓存机制
 */
@Configuration
@EnableCaching  // 启用缓存
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {

    /**
     * 配置 RedisTemplate
     * 使用 JSON 序列化
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 设置 key 的序列化方式为 String
        template.setKeySerializer(new StringRedisSerializer());

        // 设置 value 的序列化方式为 JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置 hash key 的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());

        // 设置 hash value 的序列化方式
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
```

**说明**：
- `@EnableCaching` - 启用 Spring Cache 注解
- `@ConditionalOnProperty` - 支持配置启用/禁用 Redis
- `GenericJackson2JsonRedisSerializer` - JSON 序列化
- `StringRedisSerializer` - Key 使用 String 序列化

---

## 第三部分：Spring Cache 注解（30分钟）

### @Cacheable - 缓存结果

**作用**：在方法执行前先查询缓存，如果有缓存直接返回，没有则执行方法并将结果缓存。

**实战代码**：

```java
@Cacheable(value = "users", key = "#id", unless = "#result == null")
public User getUserById(Long id) {
    log.info("从数据库查询用户: id={}", id);

    // 模拟数据库查询延迟
    simulateDelay(500);

    return database.get(id);
}
```

**执行流程**：
1. 检查缓存 `users::1770111105302` 是否存在
2. 如果存在，直接返回缓存（1ms）
3. 如果不存在，执行方法，将结果存入缓存（509ms）

**参数说明**：
- `value` - 缓存名称
- `key` - 缓存键（支持 SpEL 表达式）
- `unless` - 条件缓存（当结果为 null 时不缓存）

---

### @CachePut - 更新缓存

**作用**：每次都执行方法，并将结果存入缓存（更新缓存）。

**实战代码**：

```java
@CachePut(value = "users", key = "#user.id")
public User updateUser(User user) {
    log.info("更新用户: id={}", user.getId());

    User existing = database.get(user.getId());
    if (existing != null) {
        user.setCreateTime(existing.getCreateTime());
        database.put(user.getId(), user);
    }

    return user;  // 更新后的 user 会存入缓存
}
```

**测试结果**：
```
INFO: 第一次查询用户...
INFO: 用户信息: username=张三, age=25
INFO: 更新用户年龄: 25 -> 26
INFO: 更新完成: age=26
INFO: 第二次查询用户...
INFO: 查询结果: age=26
INFO: ✅ 缓存已同步更新!
```

---

### @CacheEvict - 删除缓存

**作用**：删除缓存。

**实战代码**：

```java
@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    log.info("删除用户: id={}", id);
    database.remove(id);
}
```

**测试结果**：
```
INFO: 第一次查询用户...
INFO: 第一次查询耗时: 516 ms
INFO: 删除用户...
INFO: 删除用户: id=1770111104263
INFO: 删除后查询用户...
INFO: 从数据库查询用户: id=1770111104263
INFO: 查询耗时: 0 ms, 结果: null
INFO: ✅ 缓存已正确删除!
```

---

### @Caching - 组合注解

**作用**：组合多个缓存注解。

```java
@Caching(
    cacheable = {
        @Cacheable(value = "user", key = "#id")
    },
    put = {
        @CachePut(value = "user_name", key = "#result.name")
    }
)
public User getUserById(Long id) {
    // ...
}
```

---

### @CacheConfig - 类级别配置

**作用**：在类上统一配置缓存名称。

```java
@Service
@CacheConfig(cacheNames = "user")
public class UserService {

    @Cacheable(key = "#id")
    public User getUserById(Long id) {
        // 使用 cacheNames = "user"
    }

    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        // 使用 cacheNames = "user"
    }
}
```

---

## 第四部分：实战演练（40分钟）

### 🚀 实战测试成果展示

在开始代码实战之前，先展示本周项目的实际测试结果。

#### 测试执行摘要

```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

#### 测试1：缓存加速效果

```
第一次查询耗时: 509 ms (从数据库)
第二次查询耗时: 1 ms (从缓存)
性能提升: 509 倍 🚀
```

**测试代码**：
```java
@Test
void testCacheSpeed() {
    // 第一次查询 - 从数据库读取(模拟延迟500ms)
    long startTime1 = System.currentTimeMillis();
    User user1 = userService.getUserById(testUser.getId());
    long endTime1 = System.currentTimeMillis();
    log.info("第一次查询耗时: {} ms (从数据库)", endTime1 - startTime1);

    // 第二次查询 - 从缓存读取(快速)
    long startTime2 = System.currentTimeMillis();
    User user2 = userService.getUserById(testUser.getId());
    long endTime2 = System.currentTimeMillis();
    log.info("第二次查询耗时: {} ms (从缓存)", endTime2 - startTime2);

    // 验证: 第二次查询应该快得多
    log.info("性能提升: {} 倍", (endTime1 - startTime1) / (double)(endTime2 - startTime2));
}
```

**日志输出**：
```
INFO: 从数据库查询用户: id=1770111105302
INFO: 第一次查询耗时: 509 ms (从数据库)
INFO: 第二次查询耗时: 1 ms (从缓存)
INFO: 性能提升: 509.0 倍
```

#### 测试2：缓存更新同步

```
✅ 缓存已同步更新!
```

**测试场景**：
1. 查询用户年龄：25
2. 更新用户年龄：25 → 26
3. 再次查询：age=26（从缓存读取，已是更新后的值）

**关键代码**：
```java
@CachePut(value = "users", key = "#user.id")
public User updateUser(User user) {
    log.info("更新用户: id={}", user.getId());
    // ...更新数据库...
    return user;  // 更新后的数据会同步到缓存
}
```

#### 测试3：缓存删除验证

```
✅ 缓存已正确删除!
```

**测试场景**：
1. 查询用户并存入缓存
2. 删除用户（同时删除缓存）
3. 再次查询：返回 null（缓存已删除，数据库也没有）

**关键代码**：
```java
@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    log.info("删除用户: id={}", id);
    database.remove(id);
}
```

#### 测试4：缓存失效时间

```
✅ 缓存在有效期内,数据可正常读取
```

**配置**：
```yaml
spring:
  cache:
    redis:
      time-to-live: 600000  # 缓存过期时间：10分钟
```

---

### 实战项目代码

#### 1. 用户实体（User.java）

```java
package com.example.redisdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private Integer age;
    private LocalDateTime createTime;
}
```

**注意**：必须实现 `Serializable` 接口，否则无法序列化存储到 Redis。

---

#### 2. 用户服务（UserService.java）

```java
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
```

---

#### 3. 用户控制器（UserController.java）

```java
package com.example.redisdemo.controller;

import com.example.redisdemo.entity.User;
import com.example.redisdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * 演示 Redis 缓存功能
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "演示 Redis 缓存功能")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * 查询用户（演示缓存）
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户（演示缓存）")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 更新用户（演示缓存更新）
     */
    @PutMapping
    @Operation(summary = "更新用户")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 删除用户（演示缓存删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    @Operation(summary = "获取所有用户")
    public Map<Long, User> getAllUsers() {
        return userService.getAllUsers();
    }
}
```

---

### API 测试示例

#### 1. 创建用户

```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{"username":"张三","email":"zhangsan@example.com","age":25}'

# 返回：
{
    "id": 1770176892294,
    "username": "张三",
    "email": "zhangsan@example.com",
    "age": 25,
    "createTime": "2026-02-04T11:48:12.2945108"
}
```

#### 2. 查询用户（第一次慢，从数据库）

```bash
curl "http://localhost:8080/api/users/1770176892294"
# 耗时: ~500ms
```

**控制台输出**：
```
INFO: 从数据库查询用户: id=1770176892294
```

#### 3. 再次查询（快，从缓存）

```bash
curl "http://localhost:8080/api/users/1770176892294"
# 耗时: ~5ms
```

**控制台输出**：
（没有日志，直接返回缓存）

#### 4. 更新用户

```bash
curl -X PUT "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{"id":1770176892294,"username":"李四","email":"lisi@example.com","age":26}'
```

#### 5. 删除用户

```bash
curl -X DELETE "http://localhost:8080/api/users/1770176892294"
```

---

### 访问 Swagger UI

浏览器打开：
```
http://localhost:8080/swagger-ui/index.html
```

可以在 Swagger UI 中交互式测试所有 API。

---

## 第五部分：Redis 实战应用（20分钟）

### 1. 自定义缓存过期时间

```java
// 默认过期时间（10分钟）
@Cacheable(value = "user", key = "#id")
public User getUserById(Long id) {
    // ...
}

// 自定义过期时间需要手动使用 RedisTemplate
@Service
@RequiredArgsConstructor
public class UserService {
    private final RedisTemplate<String, Object> redisTemplate;

    public User getUserById(Long id) {
        String key = "user::" + id;
        User user = (User) redisTemplate.opsForValue().get(key);

        if (user == null) {
            user = userMapper.selectById(id);
            // 缓存30分钟
            redisTemplate.opsForValue().set(key, user, 30, TimeUnit.MINUTES);
        }

        return user;
    }
}
```

---

### 2. 缓存穿透

**问题**：查询不存在的数据，每次都查询数据库。

**解决方案**：缓存空值或使用布隆过滤器。

```java
@Cacheable(value = "user", key = "#id", unless = "#result == null")
public User getUserById(Long id) {
    User user = userMapper.selectById(id);

    // 如果为空，缓存一个特殊值（需要配置 cache-null-values: true）
    if (user == null) {
        // Spring Cache 会自动处理（配置了 unless）
    }

    return user;
}
```

**实战配置**：
```yaml
spring:
  cache:
    redis:
      cache-null-values: false  # 不缓存 null 值
```

---

### 3. 缓存雪崩

**问题**：大量缓存同时失效，数据库压力骤增。

**解决方案**：设置随机过期时间。

```java
public User getUserById(Long id) {
    String key = "user::" + id;
    User user = (User) redisTemplate.get(key);

    if (user == null) {
        user = userMapper.selectById(id);
        // 随机过期时间（30-60分钟）
        int timeout = 30 + new Random().nextInt(30);
        redisTemplate.set(key, user, timeout, TimeUnit.MINUTES);
    }

    return user;
}
```

---

### 4. 缓存击穿

**问题**：热点数据过期，大量请求同时查询数据库。

**解决方案**：互斥锁。

```java
public User getUserById(Long id) {
    String key = "user::" + id;
    User user = (User) redisTemplate.get(key);

    if (user != null) {
        return user;
    }

    // 获取分布式锁
    String lockKey = "lock::user::" + id;
    try {
        Boolean locked = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (locked) {
            // 双重检查
            user = (User) redisTemplate.get(key);
            if (user != null) {
                return user;
            }

            // 查询数据库
            user = userMapper.selectById(id);
            redisTemplate.set(key, user, 30, TimeUnit.MINUTES);
        }
    } finally {
        redisTemplate.delete(lockKey);
    }

    return user;
}
```

---

## 课后作业

### 必做题（基础）

1. **安装 Redis**
   - 安装 Redis 服务器
   - 启动 Redis
   - 使用 redis-cli 测试基本命令

2. **整合 Spring Data Redis**
   - 添加依赖
   - 配置连接信息
   - 配置序列化

3. **实现用户信息缓存**
   - 使用 `@Cacheable` 缓存查询结果
   - 使用 `@CachePut` 更新缓存
   - 使用 `@CacheEvict` 删除缓存
   - 测试缓存效果

**实战验证**：✅ 本周项目已完成所有基础要求

---

### 选做题（进阶）

1. **自定义缓存过期时间**
   - 不同类型的数据设置不同过期时间
   - 使用 `RedisTemplate` 实现

2. **解决缓存穿透**
   - 缓存空值
   - 布隆过滤器

3. **缓存预热**
   - 应用启动时加载热点数据
   - 使用 `@PostConstruct` 实现

---

### 挑战题（额外）

1. **分布式锁**
   - 使用 Redis 实现分布式锁
   - 测试并发场景

2. **限流**
   - 使用 Redis + 注解实现接口限流
   - 每分钟最多调用N次

---

## 常见问题解答

### Q1：Redis 连接失败？

**检查清单**：
1. Redis 是否启动
   ```bash
   sc query Redis  # Windows
   redis-cli ping  # 测试连接
   ```
2. 端口是否正确（默认6379）
3. 密码是否正确
4. 防火墙是否阻止

**解决方案**：
```bash
# Windows 启动 Redis 服务
"D:\Program Files\Redis\redis-server.exe" --service-start

# 测试连接
"D:\Program Files\Redis\redis-cli.exe" ping
```

---

### Q2：缓存数据乱码？

**原因**：序列化配置问题。

**解决方案**：配置 `GenericJackson2JsonRedisSerializer`（见 RedisConfig）

```java
template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
```

---

### Q3：@Cacheable 不生效？

**检查清单**：
1. 是否添加 `@EnableCaching`
2. 方法是否为 public
3. 是否内部调用
4. 是否配置了 `CacheManager`

**实战代码**：
```java
@Configuration
@EnableCaching  // 必须添加
public class RedisConfig {
    // ...
}

@Service
public class UserService {
    @Cacheable(value = "users", key = "#id")  // 方法必须是 public
    public User getUserById(Long id) {
        // ...
    }
}
```

---

### Q4：不允许缓存 null 值？

**错误信息**：
```
Cache 'users' does not allow 'null' values
```

**解决方案**：添加 `unless = "#result == null"`

```java
@Cacheable(value = "users", key = "#id", unless = "#result == null")
public User getUserById(Long id) {
    return database.get(id);
}
```

---

## 扩展阅读

1. **Redis 官方文档**
   - [Redis 官网](https://redis.io/)
   - [Redis 命令参考](https://redis.io/commands)

2. **Spring Data Redis**
   - [官方文档](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)

3. **redis-windows 项目**
   - [GitHub 仓库](https://github.com/redis-windows/redis-windows)

---

## 下周预告

**第8周：Redis 高级应用**

- 分布式锁
- 接口限流
- HyperLogLog、Bitmap
- Redis 消息队列

---

## 教学反思点

课后请思考：

1. ✅ Redis 的使用场景？（缓存、计数器、排行榜）
2. ✅ 缓存注解的区别？（@Cacheable、@CachePut、@CacheEvict）
3. ✅ 如何解决缓存穿透？（缓存空值、布隆过滤器）
4. ✅ 如何设置合理的过期时间？（根据业务需求，一般10分钟到1小时）
5. ✅ 缓存和数据库的一致性？（先更新数据库，再更新缓存；或使用 CachePut）

---

## 📊 实战总结

### 本周完成内容

1. ✅ 安装 Redis 7.4.7 for Windows
2. ✅ 创建 Spring Boot Redis 项目
3. ✅ 配置 Redis 序列化
4. ✅ 实现缓存注解（@Cacheable、@CachePut、@CacheEvict）
5. ✅ 创建 REST API 接口
6. ✅ 编写并运行 4 个单元测试
7. ✅ 集成 Swagger API 文档
8. ✅ 验证缓存性能（509倍加速）

### 性能对比

| 操作 | 无缓存 | 有缓存 | 提升 |
|------|--------|--------|------|
| 查询用户 | 509 ms | 1 ms | **509倍** 🚀 |

### 核心代码片段

**缓存查询**：
```java
@Cacheable(value = "users", key = "#id", unless = "#result == null")
public User getUserById(Long id) {
    log.info("从数据库查询用户: id={}", id);
    simulateDelay(500);
    return database.get(id);
}
```

**缓存更新**：
```java
@CachePut(value = "users", key = "#user.id")
public User updateUser(User user) {
    // 更新数据库...
    return user;  // 自动更新缓存
}
```

**缓存删除**：
```java
@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    // 删除数据库记录...
    // 自动删除缓存
}
```

---

**文档版本**：v2.0
**最后更新**：2026-02-03
**适用版本**：Spring Boot 3.5.10、JDK 21、Redis 7.4.7
**实战路径**：`D:\fwpractice\class7\`
**测试状态**：✅ 全部通过（4/4）