# 第5周：Spring Data JPA 关联关系与实战

## 课程目标

通过本周课程，学生将学会：
1. 掌握 JPA 三种关联关系：一对一、一对多、多对多
2. 理解双向关联的工作原理和注意事项
3. 学会使用 DTO 模式解决 JSON 序列化问题
4. 掌握 JOIN FETCH 查询避免 N+1 问题
5. 学会使用 JPA 审计功能自动记录时间和操作人
6. 通过实战项目巩固所学知识

---

## 重要提示：版本兼容性

**本课程使用的稳定版本组合**：
- ✅ Spring Boot: **3.5.10** (最新稳定版)
- ✅ Spring Data JPA: **内置** (Spring Boot 管理)
- ✅ SpringDoc: **2.8.0**
- ✅ H2 Database: **内置** (用于测试)
- ✅ JDK: 21

**为什么选择 JPA 而不是 MyBatis-Plus**：
- ✅ JPA 是 Java 官方标准（JSR 338）
- ✅ Spring Boot 官方支持，版本兼容性极好
- ✅ 企业级开发主流选择
- ✅ 功能强大，适合复杂业务

---

## 第一部分：从零创建项目（20分钟）

### 步骤1：访问 Spring Initializer

#### 1.1 打开浏览器

访问：**https://start.spring.io/**

---

### 步骤2：配置项目基本信息

**填写以下信息**：

```
Project: Maven Project
Language: Java
Spring Boot: 3.5.10
Packaging: Jar
Java: 21
Configuration: YAML
Group: com.example
Artifact: jpa-advanced-demo
Name: JPA Advanced Demo
Description: Spring Data JPA 高级特性演示
Package name: com.example.jpaadvanceddemo
```

---

### 步骤3：添加依赖

搜索并添加以下 5 个依赖：

#### 依赖1：Spring Web
- 搜索：`Spring Web`
- 点击：**Spring Web**

#### 依赖2：Spring Data JPA
- 搜索：`Spring Data JPA`
- 点击：**Spring Data JPA**

#### 依赖3：Validation
- 搜索：`Validation`
- 点击：**Validation**

#### 依赖4：H2 Database
- 搜索：`H2`
- 点击：**H2 Database**

#### 依赖5：Lombok
- 搜索：`Lombok`
- 点击：**Lombok**

#### 依赖6：SpringDoc OpenAPI（手动添加）
- 如果搜索不到，稍后手动添加到 pom.xml

---

### 步骤4：生成并下载项目

1. 点击 **GENERATE** 按钮
2. 下载并解压到：`D:\fwpractice\jpa-advanced-demo\`

---

### 步骤5：导入到 IDEA

1. 打开 IDEA
2. **File** → **Open**
3. 选择 `D:\fwpractice\jpa-advanced-demo\`
4. 等待 Maven 导入完成

---

### 步骤6：手动添加 SpringDoc 依赖（如果需要）

打开 `pom.xml`，在 `<dependencies>` 中添加：

```xml
<!-- SpringDoc OpenAPI (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.0</version>
</dependency>
```

刷新 Maven。

---

### 步骤7：配置 application.yml

在 `src/main/resources` 下创建 `application.yml`：

```yaml
spring:
  application:
    name: JPA Advanced Demo

  # 数据源配置（H2 内存数据库）
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:jpadb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:

  # H2 控制台
  h2:
    console:
      enabled: true
      path: /h2-console

  # JPA 配置
  jpa:
    hibernate:
      ddl-auto: update  # 自动更新表结构（开发环境推荐）
    show-sql: true  # 显示 SQL 语句
    properties:
      hibernate:
        format_sql: true  # 格式化 SQL
        use_sql_comments: true  # 显示注释
        dialect: org.hibernate.dialect.H2Dialect
    open-in-view: false  # 避免懒加载问题

# 服务器端口
server:
  port: 8080

# 日志配置
logging:
  level:
    root: INFO
    com.example.jpaadvanceddemo: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

### 步骤8：配置 Maven 编译器

在 `pom.xml` 的 `<build><plugins>` 中添加或修改：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>21</source>
        <target>21</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.30</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

---

### 步骤9：测试运行

1. 启动应用
2. 访问 H2 控制台：http://localhost:8080/h2-console
3. 连接信息：
   - JDBC URL: `jdbc:h2:mem:jpadb`
   - 用户名: `sa`
   - 密码: (空)
4. 访问 Swagger UI：http://localhost:8080/swagger-ui/index.html

---

## 第二部分：JPA 一对一关联（30分钟）

### 业务场景

用户（User）与用户详情（UserProfile）是一对一关系。

### 理论回顾：SQL外键与关联

**什么是外键（Foreign Key）？**

外键是用于建立两个表之间关系的字段，它指向另一个表的主键。通过外键，我们可以确保数据的**参照完整性**。

**一对一关系的SQL实现**

在我们的例子中：
- `sys_user` 表存储用户的基本信息（id, username, password等）
- `user_profile` 表存储用户的详细信息（realName, idCard等）
- `user_profile` 表中的 `user_id` 字段是外键，指向 `sys_user` 表的 `id` 主键

**数据库结构示意**：

```sql
-- sys_user 表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(32) UNIQUE,
    password VARCHAR(64),
    ...
);

-- user_profile 表
CREATE TABLE user_profile (
    id BIGINT PRIMARY KEY,
    real_name VARCHAR(255),
    id_card VARCHAR(255),
    user_id BIGINT UNIQUE NOT NULL,  -- 外键，UNIQUE确保一对一关系
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);
```

**为什么是"一对一"？**

- `user_id` 上有 `UNIQUE` 约束，表示一个 user_id 只能对应一条 user_profile 记录
- 如果没有 `UNIQUE` 约束，就会变成"一对多"关系（一个用户可以有多条详情记录）

**JPA如何映射这种关系？**

- `@OneToOne` - 声明一对一关系
- `@JoinColumn(name = "user_id")` - 指定外键列名
- `unique = true` - 确保外键列的唯一性（一对一关系的核心）

### 步骤1：创建实体类

**⚠️ 重要：Lombok 实体配置要求**

在创建 JPA 实体之前，需要特别注意两个关键配置，它们会影响关联查询的稳定性：

#### 1. @EqualsAndHashCode(of = "id")

**问题**：Lombok 的 `@Data` 注解会为所有字段生成 `hashCode()` 和 `equals()` 方法。对于有集合关联（Set/List）的实体，当 Hibernate 加载这些集合时会调用 `hashCode()`，如果关联字段也被包含在 hashCode 中，会导致 `ConcurrentModificationException`。

**解决方案**：使用 `@EqualsAndHashCode(of = "id")`，只根据主键字段生成 hashCode 和 equals。

```java
@Data
@Entity
@EqualsAndHashCode(of = "id")  // ✅ 正确：只使用 id 字段
public class Student {
    private Set<Course> courses;
}

// ❌ 错误：会导致 ConcurrentModificationException
@Data
@Entity
public class Student {
    private Set<Course> courses;
}
```

#### 2. JSON 序列化策略选择

在双向关联中，需要避免：
- JSON 序列化无限递归（A→B→A→B...）
- 懒加载导致的 LazyInitializationException

**方案对比**：

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| `@JsonManagedReference` + `@JsonBackReference` | 自动处理双向关联 | 在懒加载+OSIV=false 时可能失败 | 简单项目，启用OSIV |
| `@JsonIgnore` | 完全忽略关联字段 | 需要手动创建DTO返回关联数据 | **推荐：生产环境** |

**本文采用方案**：使用 `@JsonIgnore` + **DTO模式**，这是最稳定、最可控的方式。

#### 创建 User 实体

创建 `src/main/java/com/example/jpaadvanceddemo/entity/User.java`：

```java
package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "sys_user")  // 使用 sys_user 避免 SQL 保留关键字冲突
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度必须在3-32之间")
    @Column(unique = true, nullable = false, length = 32)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度必须在6-64之间")
    @Column(nullable = false, length = 64)
    private String password;

    @Email(message = "邮箱格式不正确")
    @Column(length = 64)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;  // 0-禁用，1-正常

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateTime;

    // 一对一关联：用户详情
    // mappedBy = "user" 表示关系由 UserProfile 的 user 属性维护
    @JsonIgnore  // 避免JSON序列化时触发懒加载问题
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;
}
```

#### 创建 UserProfile 实体

创建 `src/main/java/com/example/jpaadvanceddemo/entity/UserProfile.java`：

```java
package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 用户详情实体
 */
@Data
@Entity
@Table(name = "user_profile")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String realName;
    private String idCard;
    private LocalDate birthday;

    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    @Column(columnDefinition = "TINYINT DEFAULT 2")
    private Integer gender = 2;  // 0-女，1-男，2-未知

    private String address;
    private String avatar;

    // 一对一关联：用户
    // @JoinColumn 指定外键列名
    // unique = true 确保一对一关系
    @JsonIgnore  // 避免JSON序列化无限递归
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
}
```

**注解说明**：

- `@OneToOne` - 一对一关联
  - `cascade = CascadeType.ALL` - 级联所有操作（保存、删除、更新等）
  - `fetch = FetchType.LAZY` - 懒加载（默认）

- `@JoinColumn` - 定义外键列
  - `name = "user_id"` - 外键列名
  - `unique = true` - 确保一对一关系
  - `nullable = false` - 不能为空

- `mappedBy` - 关系的维护端
  - 在 User 中使用 `mappedBy = "user"` 表示由 UserProfile 的 user 属性维护关系
  - 这样定义后，保存时只需要保存 UserProfile

---

### 步骤2：创建 Repository

创建 `src/main/java/com/example/jpaadvanceddemo/repository/UserRepository.java`：

```java
package com.example.jpaadvanceddemo.repository;

import com.example.jpaadvanceddemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名查询（自动生成实现）
    Optional<User> findByUsername(String username);

    // 根据邮箱查询
    Optional<User> findByEmail(String email);

    // JPQL 查询：查询用户及其详情（使用 JOIN FETCH 避免N+1问题）
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userProfile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") Long id);

    // JPQL 查询：查询所有用户及其详情
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userProfile")
    List<User> findAllWithProfile();
}
```

#### 创建 UserProfileRepository

虽然 `UserProfile` 是从属实体，但在创建用户时我们需要显式保存它（原因见后文）。创建 `src/main/java/com/example/jpaadvanceddemo/repository/UserProfileRepository.java`：

```java
package com.example.jpaadvanceddemo.repository;

import com.example.jpaadvanceddemo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户详情 Repository
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
```

### JPQL 查询理论讲解

**什么是 JPQL？**

JPQL（Java Persistence Query Language）是JPA提供的查询语言，语法类似SQL，但操作的是**实体对象**而非数据库表。

**为什么需要 JOIN FETCH？**

这是解决 **N+1 查询问题** 的关键。

**问题演示：N+1 查询**

```java
// ❌ 错误方式：会产生N+1问题
List<User> users = userRepository.findAll();
for (User user : users) {
    // 每次访问 user.getUserProfile() 都会触发一次额外的SQL查询
    UserProfile profile = user.getUserProfile();
}
```

执行日志：
```sql
-- 第1条SQL：查询所有用户
SELECT * FROM sys_user;

-- 第2到N+1条SQL：为每个用户查询详情（N次额外查询）
SELECT * FROM user_profile WHERE user_id = 1;
SELECT * FROM user_profile WHERE user_id = 2;
SELECT * FROM user_profile WHERE user_id = 3;
...
```

如果有100个用户，就会执行 **101条SQL**（1条查询用户 + 100条查询详情）！

**解决方案：使用 JOIN FETCH**

```java
// ✅ 正确方式：使用 JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.userProfile WHERE u.id = :id")
Optional<User> findByIdWithProfile(@Param("id") Long id);
```

**JPQL语句解析**：

```java
"SELECT u FROM User u LEFT JOIN FETCH u.userProfile WHERE u.id = :id"
```

| 关键词 | 说明 |
|--------|------|
| `SELECT u` | 选择 User 对象 |
| `FROM User u` | 从 User 实体查询，u 是别名 |
| `LEFT JOIN` | 左外连接（即使没有详情也会返回用户） |
| `FETCH` | **关键**：立即加载关联对象，避免懒加载 |
| `u.userProfile` | 关联的属性 |
| `WHERE u.id = :id` | 条件过滤，:id 是命名参数 |

生成的SQL：
```sql
-- 只需1条SQL，同时查询用户和详情
SELECT u.*, p.*
FROM sys_user u
LEFT OUTER JOIN user_profile p ON u.id = p.user_id
WHERE u.id = ?;
```

**查询多个用户时为什么要用 DISTINCT？**

```java
@Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userProfile")
List<User> findAllWithProfile();
```

当使用 `LEFT JOIN FETCH` 时，如果用户有详情，结果集可能包含重复的用户对象：
```sql
-- SQL层面的结果
u.id=1, u.username='zhangsan', p.id=1, p.realName='张三'  -- 第一条
u.id=1, u.username='zhangsan', p.id=1, p.realName='张三'  -- 可能重复（因为JOIN）
```

`DISTINCT` 告诉JPA在返回结果前去除重复的User对象（在Java层面去重，而非SQL层面）。

**JPQL vs SQL 对比**

| 特性 | JPQL | SQL |
|------|------|-----|
| 操作对象 | 实体类（User） | 数据库表（sys_user） |
| 字段引用 | 属性名（username） | 列名（username） |
| 端口性 | 支持不同数据库 | 特定数据库语法 |
| 关联查询 | 对象属性导航（u.userProfile） | 表连接（JOIN sys_user u ...） |

---

### 步骤3：创建 Service

#### 创建 UserService 接口

创建 `src/main/java/com/example/jpaadvanceddemo/service/UserService.java`：

```java
package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.entity.User;

import java.util.List;

/**
 * 用户 Service 接口
 */
public interface UserService {

    /**
     * 创建用户
     */
    User createUser(User user);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据ID查询用户及其详情
     */
    User getUserByIdWithProfile(Long id);

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 更新用户
     */
    User updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);
}
```

#### 创建 UserServiceImpl 实现类

创建 `src/main/java/com/example/jpaadvanceddemo/service/impl/UserServiceImpl.java`：

```java
package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.entity.User;
import com.example.jpaadvanceddemo.repository.UserRepository;
import com.example.jpaadvanceddemo.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User getUserByIdWithProfile(Long id) {
        return userRepository.findByIdWithProfile(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

---

### 步骤4：创建 Controller

创建 `src/main/java/com/example/jpaadvanceddemo/controller/UserController.java`：

```java
package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.entity.User;
import com.example.jpaadvanceddemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户CRUD操作")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "创建用户")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public User getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/with-profile")
    @Operation(summary = "查询用户及其详情")
    public User getUserByIdWithProfile(
            @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        return userService.getUserByIdWithProfile(id);
    }

    @GetMapping
    @Operation(summary = "查询所有用户")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping
    @Operation(summary = "更新用户")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        userService.deleteUser(id);
    }
}
```

---

### 步骤5：创建 DTO（数据传输对象）

**为什么需要 DTO？**

由于我们在 User 和 UserProfile 实体的关联字段上使用了 `@JsonIgnore`，直接返回实体时不会包含关联数据。因此需要 DTO 来：
1. **接收请求**：创建用户时需要接收 userProfile 数据
2. **返回响应**：查询用户时需要返回 userProfile 数据

#### 创建 UserCreateRequestDTO

创建 `src/main/java/com/example/jpaadvanceddemo/dto/UserCreateRequestDTO.java`：

```java
package com.example.jpaadvanceddemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建用户请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDTO {

    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status;

    private UserProfileDTO userProfile;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileDTO {
        private String realName;
        private Integer gender;
        private String birthday;  // 使用 String 类型，方便 JSON 序列化
        private String address;
    }
}
```

#### 创建 UserWithProfileDTO

创建 `src/main/java/com/example/jpaadvanceddemo/dto/UserWithProfileDTO.java`：

```java
package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户 DTO（包含用户详情）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithProfileDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private String createTime;
    private String updateTime;
    private UserProfile userProfile;
}
```

#### 扩展 UserService 接口

在 `UserService.java` 中添加两个新方法：

```java
/**
 * 使用 DTO 创建用户
 */
User createUserWithDTO(UserCreateRequestDTO dto);

/**
 * 根据ID查询用户及其详情（返回DTO）
 */
UserWithProfileDTO getUserByIdWithProfileDTO(Long id);
```

#### 扩展 UserServiceImpl 实现类

在 `UserServiceImpl.java` 中添加 `UserProfileRepository` 依赖并实现新方法：

```java
private final UserRepository userRepository;
private final UserProfileRepository userProfileRepository;  // 新增

@Override
@Transactional
public User createUserWithDTO(UserCreateRequestDTO dto) {
    // 创建用户
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword());
    user.setEmail(dto.getEmail());
    user.setPhone(dto.getPhone());
    user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

    // 先保存用户（需要先有用户ID）
    User savedUser = userRepository.save(user);

    // 如果有 profile 数据，创建并保存
    if (dto.getUserProfile() != null) {
        UserProfile profile = new UserProfile();
        profile.setRealName(dto.getUserProfile().getRealName());
        profile.setGender(dto.getUserProfile().getGender());
        profile.setBirthday(LocalDate.parse(dto.getUserProfile().getBirthday()));
        profile.setAddress(dto.getUserProfile().getAddress());
        profile.setUser(savedUser);

        userProfileRepository.save(profile);
    }

    // 重新查询以获取完整的关联数据
    return userRepository.findByIdWithProfile(savedUser.getId())
            .orElse(savedUser);
}

@Override
public UserWithProfileDTO getUserByIdWithProfileDTO(Long id) {
    User user = userRepository.findByIdWithProfile(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

    // 手动构建 DTO
    UserWithProfileDTO dto = new UserWithProfileDTO();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setPhone(user.getPhone());
    dto.setStatus(user.getStatus());
    dto.setCreateTime(user.getCreateTime().toString());
    dto.setUpdateTime(user.getUpdateTime().toString());
    dto.setUserProfile(user.getUserProfile());

    return dto;
}
```

**注意**：需要在 `UserServiceImpl` 顶部添加导入：
```java
import com.example.jpaadvanceddemo.dto.UserCreateRequestDTO;
import com.example.jpaadvanceddemo.dto.UserWithProfileDTO;
import com.example.jpaadvanceddemo.entity.UserProfile;
import com.example.jpaadvanceddemo.repository.UserProfileRepository;
import java.time.LocalDate;
```

#### 更新 UserController

在 `UserController.java` 中添加新的接口：

```java
@PostMapping
@Operation(summary = "创建用户")
public User createUser(@RequestBody UserCreateRequestDTO dto) {
    return userService.createUserWithDTO(dto);
}

@GetMapping("/{id}/with-profile-dto")
@Operation(summary = "查询用户及其详情（返回DTO）")
public UserWithProfileDTO getUserByIdWithProfileDTO(
        @Parameter(description = "用户ID") @PathVariable Long id
) {
    return userService.getUserByIdWithProfileDTO(id);
}
```

同时需要在 `UserController` 顶部添加导入：
```java
import com.example.jpaadvanceddemo.dto.UserCreateRequestDTO;
import com.example.jpaadvanceddemo.dto.UserWithProfileDTO;
```

**DTO 模式的优势**：

1. **清晰的职责分离**：
   - Entity：只负责数据库映射
   - DTO：负责数据传输
   - Service：负责业务逻辑和数据转换

2. **避免 JPA 懒加载问题**：
   - 不需要担心 `@JsonIgnore` 影响反序列化
   - 不需要处理复杂的双向关联

3. **更好的安全性**：
   - 可以控制哪些字段可以接收
   - 避免恶意用户提交不应该修改的字段（如 id、createTime）

4. **灵活性**：
   - DTO 结构可以与实体结构不同
   - 日期字段可以使用 String 类型，更方便前端处理
### 步骤6：创建测试数据（可选）

为了方便测试，我们可以在应用启动时自动创建一些测试数据。创建 `src/main/java/com/example/jpaadvanceddemo/config/DataInitializer.java`（这个文件在后面的一对多和多对多部分还会扩展）：

```java
package com.example.jpaadvanceddemo.config;

import com.example.jpaadvanceddemo.entity.User;
import com.example.jpaadvanceddemo.entity.UserProfile;
import com.example.jpaadvanceddemo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 检查是否已有数据，避免重复初始化
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("开始初始化测试数据...");

        // 创建用户1及其详情
        User user1 = new User();
        user1.setUsername("zhangsan");
        user1.setPassword("123456");
        user1.setEmail("zhangsan@example.com");
        user1.setPhone("13800138000");
        user1.setStatus(1);

        UserProfile profile1 = new UserProfile();
        profile1.setRealName("张三");
        profile1.setGender(1);
        profile1.setBirthday(LocalDate.parse("1990-01-01"));
        profile1.setAddress("北京市朝阳区");
        profile1.setUser(user1);
        user1.setUserProfile(profile1);

        // 创建用户2及其详情
        User user2 = new User();
        user2.setUsername("lisi");
        user2.setPassword("123456");
        user2.setEmail("lisi@example.com");
        user2.setPhone("13800138001");
        user2.setStatus(1);

        UserProfile profile2 = new UserProfile();
        profile2.setRealName("李四");
        profile2.setGender(0);
        profile2.setBirthday(LocalDate.parse("1992-05-15"));
        profile2.setAddress("上海市浦东新区");
        profile2.setUser(user2);
        user2.setUserProfile(profile2);

        // 创建用户3及其详情
        User user3 = new User();
        user3.setUsername("wangwu");
        user3.setPassword("123456");
        user3.setEmail("wangwu@example.com");
        user3.setPhone("13800138002");
        user3.setStatus(1);

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
```

**说明**：
- 这个类会在应用启动后自动运行
- 创建了3个测试用户，每个用户都有对应的详情
- 手动设置了双向关联关系（`user.setUserProfile(profile)` 和 `profile.setUser(user)`）

---

### 步骤7：测试一对一关联

重启应用后，测试数据会自动创建。然后使用 Swagger UI 或 Postman 测试：

#### 测试1：创建新用户（包含详情）

**POST** `/api/users`

```json
{
  "username": "xiaoming",
  "password": "123456",
  "email": "xiaoming@example.com",
  "phone": "13900139000",
  "status": 1,
  "userProfile": {
    "realName": "小明",
    "gender": 1,
    "birthday": "1995-06-15",
    "address": "深圳市南山区"
  }
}
```

**预期响应**：
```json
{
  "id": 4,
  "username": "xiaoming",
  "email": "xiaoming@example.com",
  "phone": "13900139000",
  "status": 1,
  "createTime": "2026-02-02T...",
  "updateTime": "2026-02-02T...",
  "userProfile": {
    "id": 4,
    "realName": "小明",
    "gender": 1,
    "birthday": "1995-06-15",
    "address": "深圳市南山区"
  }
}
```

#### 测试2：查询所有用户

**GET** `/api/users`

返回所有用户列表

#### 测试3：查询单个用户（不包含详情）

**GET** `/api/users/1`

返回的用户对象中 `userProfile` 不会出现在 JSON 中（因为使用了 `@JsonIgnore`）

#### 测试4：查询用户及其详情（使用 JOIN FETCH）

**GET** `/api/users/1/with-profile`

由于使用了 `@JsonIgnore`，返回的用户对象中仍然**不会包含** `userProfile` 字段。

#### 测试5：查询用户及其详情（返回DTO）

**GET** `/api/users/1/with-profile-dto`

返回的 DTO 对象中包含完整的 `userProfile`（推荐方式）

**预期响应**：

```json
{
  "id": 1,
  "username": "zhangsan",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "status": 1,
  "createTime": "2026-02-02T...",
  "updateTime": "2026-02-02T...",
  "userProfile": {
    "id": 1,
    "realName": "张三",
    "gender": 1,
    "birthday": "1990-01-01",
    "address": "北京市朝阳区"
  }
}
```

---

## 第三部分：JPA 一对多关联（40分钟）

### 业务场景

部门（Department）与员工（Employee）是一对多关系。

### 理论回顾：一对多关系的SQL实现

**一对多关系的特点**

一对多是最常见的关联关系：
- "一"方（部门）：一个部门可以有多个员工
- "多"方（员工）：一个员工只能属于一个部门

**数据库结构示意**：

```sql
-- department 表（"一"方）
CREATE TABLE department (
    id BIGINT PRIMARY KEY,
    dept_name VARCHAR(64) NOT NULL,
    description VARCHAR(255)
);

-- employee 表（"多"方）
CREATE TABLE employee (
    id BIGINT PRIMARY KEY,
    emp_name VARCHAR(32) NOT NULL,
    position VARCHAR(255),
    salary FLOAT,
    dept_id BIGINT NOT NULL,  -- 外键，但不需要UNIQUE约束
    FOREIGN KEY (dept_id) REFERENCES department(id)
);
```

**关键区别：为什么不需要 UNIQUE 约束？**

- 一对一：外键有 `UNIQUE` 约束（一个用户ID只能对应一条详情记录）
- 一对多：外键**没有** `UNIQUE` 约束（一个部门ID可以对应多条员工记录）

**数据示例**：

| department | employee |
|------------|----------|
| id=1, 研发部 | dept_id=1, 张三 |
| | dept_id=1, 李四 |
| | dept_id=1, 王五 |

同一个 `dept_id=1` 在 employee 表中出现了多次，这就是"一对多"的本质。

**JPA如何映射？**

- Department（"一"方）使用 `@OneToMany(mappedBy = "department")`
- Employee（"多"方）使用 `@ManyToOne` + `@JoinColumn(name = "dept_id")`
- 外键列在"多"方（employee表）中维护

### 步骤1：创建实体类

#### 创建 Department 实体

创建 `src/main/java/com/example/jpaadvanceddemo/entity/Department.java`：

```java
package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体
 */
@Data
@Entity
@Table(name = "department")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    @Column(nullable = false, length = 64)
    private String deptName;

    private String description;

    // 一对多关联：员工列表
    // mappedBy = "department" 表示关系由 Employee 的 department 属性维护
    // 使用 @JsonIgnore 避免以下问题：
    // 1. JSON序列化无限递归（Department→Employee→Department→Employee...）
    // 2. 懒加载导致的 LazyInitializationException（当 open-in-view=false 时）
    // 3. 如果需要返回关联数据，应使用 DTO（Data Transfer Object）
    @JsonIgnore
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
}
```

#### 创建 Employee 实体

创建 `src/main/java/com/example/jpaadvanceddemo/entity/Employee.java`：

```java
package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工实体
 */
@Data
@Entity
@Table(name = "employee")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "员工姓名不能为空")
    @Column(nullable = false, length = 32)
    private String empName;

    private String position;
    private Double salary;

    // 多对一关联：所属部门
    // 使用 @JsonIgnore 避免JSON序列化无限递归
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;
}
```

**注解说明**：

- `@OneToMany` - 一对多关联
  - `mappedBy = "department"` - 关系由 Employee 维护
  - `cascade = CascadeType.ALL` - 级联删除部门时，也删除所有员工
  - `fetch = FetchType.LAZY` - 懒加载（默认）

- `@ManyToOne` - 多对一关联
  - `fetch = FetchType.LAZY` - 懒加载（默认）
  - `@JoinColumn(name = "dept_id")` - 外键列名

---

### 步骤2：创建 Repository

#### DepartmentRepository

```java
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // 查询部门及其员工（使用 JOIN FETCH 避免N+1问题）
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Optional<Department> findByIdWithEmployees(@Param("id") Long id);

    // 查询所有部门及其员工
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();
}
```

### JPQL 查询理论讲解（一对多）

**一对多关系的 N+1 问题更加严重**

```java
// ❌ 错误方式：会产生严重的N+1问题
List<Department> departments = departmentRepository.findAll();
for (Department dept : departments) {
    // 每个部门访问员工列表时，都会触发一次额外的SQL查询
    List<Employee> employees = dept.getEmployees();
}
```

假设有10个部门，每个部门平均有20个员工：
- 第1条SQL：查询所有部门
- 第2-11条SQL：为每个部门查询员工列表（10次额外查询）
- **总计11条SQL**

**如果进一步访问员工信息**，问题会更严重：
```java
for (Department dept : departments) {
    for (Employee emp : dept.getEmployees()) {
        // 每个员工访问所属部门时，又可能触发新的查询！
        Department d = emp.getDepartment();
    }
}
```

**解决方案：使用 JOIN FETCH**

```java
@Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
Optional<Department> findByIdWithEmployees(@Param("id") Long id);
```

**JPQL语句解析**：

```java
"SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id"
```

| 关键词 | 说明 |
|--------|------|
| `SELECT d` | 选择 Department 对象 |
| `FROM Department d` | 从 Department 实体查询 |
| `LEFT JOIN FETCH` | 左外连接 + 立即加载 |
| `d.employees` | 关联的集合属性（一对多的"多"方） |
| `WHERE d.id = :id` | 条件过滤 |

生成的SQL：
```sql
-- 只需1条SQL，同时查询部门和所有员工
SELECT d.*, e.*
FROM department d
LEFT OUTER JOIN employee e ON d.id = e.dept_id
WHERE d.id = ?;
```

**为什么一对多必须用 DISTINCT？**

一对多关系中使用 `JOIN FETCH` 时，**必须使用 DISTINCT**：

```java
// ✅ 正确：使用 DISTINCT
@Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
List<Department> findAllWithEmployees();

// ❌ 错误：不用 DISTINCT 会导致重复
@Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees")
List<Department> findAllWithEmployees();  // 返回列表中会有重复的Department对象！
```

**原因示例**：

假设"研发部"有3个员工（张三、李四、王五），SQL执行结果：

| d.id | d.dept_name | e.id | e.emp_name |
|------|-------------|------|------------|
| 1 | 研发部 | 1 | 张三 |
| 1 | 研发部 | 2 | 李四 |
| 1 | 研发部 | 3 | 王五 |

SQL层面返回了3行记录，但我们需要的是 **1个Department对象**（包含3个Employee）。

`DISTINCT` 告诉Hibernate：在返回Java对象前，去除重复的Department对象。

**查询所有部门及其员工（完整示例）**

```java
@Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

这会生成1条SQL：
```sql
SELECT d.*, e.*
FROM department d
LEFT OUTER JOIN employee e ON d.id = e.dept_id;
```

Hibernate会在内存中组装：
- Department(id=1, deptName="研发部")
  - employees: [张三, 李四, 王五]
- Department(id=2, deptName="销售部")
  - employees: [赵六, 钱七]

---

### 步骤3：查询性能最佳实践

**何时使用 JOIN FETCH？**

不是所有查询都需要 JOIN FETCH，需要根据实际场景选择：

| 场景 | 查询方式 | 原因 |
|------|----------|------|
| 只需要部门列表 | `findAll()` | 不需要员工信息，避免不必要的数据加载 |
| 需要显示部门+员工数 | `findAllWithEmployees()` | 需要员工信息，使用 JOIN FETCH |
| 只查询单个部门+员工 | `findByIdWithEmployees(id)` | 单个查询，JOIN FETCH 性能更好 |
| 分页查询部门 | `findAll()` + @EntityGraph | 分页场景推荐使用 EntityGraph |

**@EntityGraph：另一种选择**

除了 JPQL 的 JOIN FETCH，还可以使用 `@EntityGraph`：

```java
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // 使用 EntityGraph 加载关联
    @EntityGraph(attributePaths = {"employees"})
    List<Department> findAll();
}
```

**JOIN FETCH vs @EntityGraph 对比**：

| 特性 | JOIN FETCH | @EntityGraph |
|------|------------|--------------|
| 复杂度 | JPQL中指定 | 注解声明，更简洁 |
| 灵活性 | 高（可自定义查询） | 中（固定加载路径） |
| 可读性 | 中（查询较长） | 高（声明式） |
| 适用场景 | 复杂查询、自定义加载 | 简单查询、固定加载模式 |

**性能测试建议**

使用 Spring Boot 的日志统计来验证优化效果：

```yaml
# application.yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.stat: DEBUG
```

启动应用后访问 `/api/departments`，观察日志：
- 未优化：会看到多条 SELECT employee 语句
- 已优化：只会看到一条 JOIN 查询

**常见陷阱**

1. **在事务外访问懒加载属性**（会抛出 LazyInitializationException）
```java
// ❌ 错误：事务已结束
@Transactional
public Department getDepartment(Long id) {
    return departmentRepository.findById(id).orElseThrow();
}
// 调用方访问 dept.getEmployees() 会报错！

// ✅ 正确：在事务内完成加载
@Transactional
public Department getDepartmentWithEmployees(Long id) {
    return departmentRepository.findByIdWithEmployees(id).orElseThrow();
}
```

2. **过度使用 eager 加载**
```java
// ❌ 避免：默认改为 EAGER 会导致所有查询都加载员工
@OneToMany(fetch = FetchType.EAGER)  // 不推荐
private List<Employee> employees;

// ✅ 推荐：保持 LAZY，按需使用 JOIN FETCH
@OneToMany(fetch = FetchType.LAZY)  // 默认值
private List<Employee> employees;
```

---

### 步骤4：创建 Service

#### DepartmentService 接口

创建 `src/main/java/com/example/jpaadvanceddemo/service/DepartmentService.java`：

```java
package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.entity.Department;

import java.util.List;

/**
 * 部门 Service 接口
 */
public interface DepartmentService {

    /**
     * 创建部门（包含员工）
     */
    Department createDepartment(Department department);

    /**
     * 根据ID查询部门
     */
    Department getDepartmentById(Long id);

    /**
     * 根据ID查询部门及其员工
     */
    Department getDepartmentByIdWithEmployees(Long id);

    /**
     * 查询所有部门
     */
    List<Department> getAllDepartments();

    /**
     * 更新部门
     */
    Department updateDepartment(Department department);

    /**
     * 删除部门
     */
    void deleteDepartment(Long id);
}
```

#### DepartmentServiceImpl 实现类

创建 `src/main/java/com/example/jpaadvanceddemo/service/impl/DepartmentServiceImpl.java`：

```java
package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.entity.Department;
import com.example.jpaadvanceddemo.entity.Employee;
import com.example.jpaadvanceddemo.repository.DepartmentRepository;
import com.example.jpaadvanceddemo.service.DepartmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public Department createDepartment(Department department) {
        // 手动设置双向关联关系
        if (department.getEmployees() != null) {
            for (Employee employee : department.getEmployees()) {
                employee.setDepartment(department);
            }
        }
        return departmentRepository.save(department);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
    }

    @Override
    public Department getDepartmentByIdWithEmployees(Long id) {
        return departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional
    public Department updateDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}
```

---

### 步骤5：创建 Controller

创建 `src/main/java/com/example/jpaadvanceddemo/controller/DepartmentController.java`：

```java
package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.entity.Department;
import com.example.jpaadvanceddemo.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理", description = "部门相关接口")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "创建部门")
    public Department createDepartment(@Valid @RequestBody Department department) {
        return departmentService.createDepartment(department);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询部门")
    public Department getDepartmentById(
            @Parameter(description = "部门ID") @PathVariable Long id
    ) {
        return departmentService.getDepartmentById(id);
    }

    @GetMapping("/{id}/with-employees")
    @Operation(summary = "查询部门及其员工")
    public Department getDepartmentByIdWithEmployees(
            @Parameter(description = "部门ID") @PathVariable Long id
    ) {
        return departmentService.getDepartmentByIdWithEmployees(id);
    }

    @GetMapping
    @Operation(summary = "查询所有部门")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PutMapping
    @Operation(summary = "更新部门")
    public Department updateDepartment(@RequestBody Department department) {
        return departmentService.updateDepartment(department);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public void deleteDepartment(
            @Parameter(description = "部门ID") @PathVariable Long id
    ) {
        departmentService.deleteDepartment(id);
    }
}
```

---

### 步骤6：创建 DTO（数据传输对象）

**为什么需要 DTO？**

由于我们在实体类的关联字段上使用了 `@JsonIgnore`，直接返回实体时不会包含关联数据。为了在需要返回关联数据时能够正常工作，我们需要创建 DTO（Data Transfer Object）。

**DTO 的作用**：
1. 解耦实体和API响应：实体负责数据持久化，DTO负责数据传输
2. 精确控制返回字段：可以自定义返回哪些数据
3. 避免懒加载问题：DTO在事务内构建，数据已经加载完成
4. 提高性能：只返回需要的数据，减少传输量

#### 创建 DepartmentWithEmployeesDTO

创建 `src/main/java/com/example/jpaadvanceddemo/dto/DepartmentWithEmployeesDTO.java`：

```java
package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 部门 DTO（包含员工列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentWithEmployeesDTO {

    private Long id;
    private String deptName;
    private String description;
    private List<Employee> employees;
}
```

注意：DTO 中的 employees 字段没有 `@JsonIgnore`，所以会正常序列化。

#### 修改 DepartmentService 接口

添加返回 DTO 的方法：

```java
/**
 * 根据ID查询部门及其员工（返回 DTO）
 */
DepartmentWithEmployeesDTO getDepartmentByIdWithEmployeesDTO(Long id);
```

#### 修改 DepartmentServiceImpl 实现类

添加 DTO 转换方法：

```java
@Override
public DepartmentWithEmployeesDTO getDepartmentByIdWithEmployeesDTO(Long id) {
    Department department = departmentRepository.findByIdWithEmployees(id)
            .orElseThrow(() -> new RuntimeException("部门不存在"));

    // 手动构建 DTO
    DepartmentWithEmployeesDTO dto = new DepartmentWithEmployeesDTO();
    dto.setId(department.getId());
    dto.setDeptName(department.getDeptName());
    dto.setDescription(department.getDescription());
    dto.setEmployees(department.getEmployees());

    return dto;
}
```

#### 修改 DepartmentController

添加返回 DTO 的接口：

```java
@GetMapping("/{id}/with-employees-dto")
@Operation(summary = "查询部门及其员工（返回DTO）")
public DepartmentWithEmployeesDTO getDepartmentByIdWithEmployeesDTO(
        @Parameter(description = "部门ID") @PathVariable Long id
) {
    return departmentService.getDepartmentByIdWithEmployeesDTO(id);
}
```

**测试对比**：

1. **测试实体接口**（员工列表被忽略）：
   - GET `/api/departments/1/with-employees`
   - 返回：`{"id":1,"deptName":"研发部","description":"..."}`（没有 employees）

2. **测试 DTO 接口**（员工列表正常返回）：
   - GET `/api/departments/1/with-employees-dto`
   - 返回：`{"id":1,"deptName":"研发部","description":"...","employees":[...]}`（包含员工列表）

---

### 步骤7：扩展数据初始化器

**注意**：在一对一部分已经创建了 `DataInitializer.java`，现在需要扩展它以支持部门-员工数据。

修改 `DataInitializer.java`，添加部门-员工的初始化方法：

```java
// ... 现有代码 ...

private final DepartmentRepository departmentRepository;
private final EmployeeRepository employeeRepository;

// 在 run() 方法中调用新方法
@Override
@Transactional
public void run(String... args) throws Exception {
    if (userRepository.count() > 0) {
        return;
    }

    System.out.println("===========================================");
    System.out.println("开始初始化测试数据...");
    System.out.println("===========================================");

    initUserAndProfile();      // 一对一数据（已存在）
    initDepartmentAndEmployee(); // 一对多数据（新增）

    // ... 其他初始化方法
}

/**
 * 初始化部门和员工数据（一对多关系）
 */
private void initDepartmentAndEmployee() {
    System.out.println("\n【一对多：部门与员工】");

    // 创建部门
    Department dept1 = new Department();
    dept1.setDeptName("研发部");
    dept1.setDescription("负责产品研发和技术创新");

    Department dept2 = new Department();
    dept2.setDeptName("销售部");
    dept2.setDescription("负责市场销售和客户关系");

    Department dept3 = new Department();
    dept3.setDeptName("人力资源部");
    dept3.setDescription("负责人员招聘和培训");

    departmentRepository.save(dept1);
    departmentRepository.save(dept2);
    departmentRepository.save(dept3);

    System.out.println("✓ 创建了3个部门：研发部、销售部、人力资源部");

    // 创建员工
    Employee emp1 = new Employee();
    emp1.setEmpName("张三");
    emp1.setPosition("高级Java工程师");
    emp1.setSalary(25000.0);
    emp1.setDepartment(dept1);

    Employee emp2 = new Employee();
    emp2.setEmpName("李四");
    emp2.setPosition("前端工程师");
    emp2.setSalary(20000.0);
    emp2.setDepartment(dept1);

    Employee emp3 = new Employee();
    emp3.setEmpName("王五");
    emp3.setPosition("测试工程师");
    emp3.setSalary(18000.0);
    emp3.setDepartment(dept1);

    Employee emp4 = new Employee();
    emp4.setEmpName("赵六");
    emp4.setPosition("销售经理");
    emp4.setSalary(22000.0);
    emp4.setDepartment(dept2);

    Employee emp5 = new Employee();
    emp5.setEmpName("钱七");
    emp5.setPosition("销售代表");
    emp5.setSalary(15000.0);
    emp5.setDepartment(dept2);

    Employee emp6 = new Employee();
    emp6.setEmpName("孙八");
    emp6.setPosition("HR专员");
    emp6.setSalary(16000.0);
    emp6.setDepartment(dept3);

    employeeRepository.save(emp1);
    employeeRepository.save(emp2);
    employeeRepository.save(emp3);
    employeeRepository.save(emp4);
    employeeRepository.save(emp5);
    employeeRepository.save(emp6);

    System.out.println("✓ 创建了6名员工");
    System.out.println("  - 研发部：张三、李四、王五");
    System.out.println("  - 销售部：赵六、钱七");
    System.out.println("  - 人力资源部：孙八");
}
```

**说明**：
- 这是对已有 `DataInitializer` 的扩展，不是创建新文件
- 添加了 `departmentRepository` 和 `employeeRepository` 依赖
- 在 `run()` 方法中调用 `initDepartmentAndEmployee()`

---

### 步骤8：测试一对多关联

使用 Swagger UI 测试：http://localhost:8080/swagger-ui/index.html

#### 测试1：查询所有部门（不包含员工）

**GET** `/api/departments`

返回结果中每个部门的 `employees` 字段不会出现在 JSON 中（因为使用了 `@JsonIgnore`）

#### 测试2：查询部门及其员工（使用 JOIN FETCH）

**GET** `/api/departments/1/with-employees`

注意：由于使用了 `@JsonIgnore`，返回的部门对象中仍然**不会包含** `employees` 字段。

**解决方案**：使用 DTO 接口获取完整数据（见测试3）。

#### 测试2.1：查询部门及其员工（使用 DTO）

**GET** `/api/departments/1/with-employees-dto`

返回的 DTO 对象中包含完整的 `employees` 列表（推荐方式）

#### 测试3：创建新部门并添加员工

**POST** `/api/departments`

```json
{
  "deptName": "技术部",
  "description": "负责技术研发",
  "employees": [
    {
      "empName": "赵六",
      "position": "架构师",
      "salary": 35000.0
    },
    {
      "empName": "钱七",
      "position": "高级工程师",
      "salary": 28000.0
    }
  ]
}
```

观察 H2 控制台中的 SQL 日志，验证是否只执行了 1 条 INSERT 语句（包含部门+员工）。

---

## 第四部分：JPA 多对多关联（40分钟）

### 业务场景

学生（Student）与课程（Course）是多对多关系。

### 理论回顾：多对多关系的SQL实现

**多对多关系的特点**

多对多关系需要一个**中间表（Join Table）**来连接两个实体：
- 一个学生可以选修多门课程
- 一门课程可以被多个学生选修

**数据库结构示意**：

```sql
-- student 表
CREATE TABLE student (
    id BIGINT PRIMARY KEY,
    student_name VARCHAR(32) NOT NULL,
    student_no VARCHAR(32)
);

-- course 表
CREATE TABLE course (
    id BIGINT PRIMARY KEY,
    course_name VARCHAR(64) NOT NULL,
    credit INTEGER
);

-- 中间表（关联表）
CREATE TABLE student_course (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),  -- 联合主键
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);
```

**中间表的作用**

中间表 `student_course` 只存储两个实体的ID关联：
- `student_id`：指向 student 表
- `course_id`：指向 course 表
- 联合主键 `(student_id, course_id)`：确保同一个学生不会重复选同一门课

**数据示例**：

| student | course | student_course（中间表）|
|---------|--------|---------------------|
| id=1, 张三 | id=101, Java | student_id=1, course_id=101 |
| id=2, 李四 | id=102, Python | student_id=1, course_id=102 |
| | id=103, MySQL | student_id=2, course_id=101 |
| | | student_id=2, course_id=103 |

张三选了 Java 和 Python，李四选了 Java 和 MySQL。

**JPA如何映射？**

- `@ManyToMany` - 声明多对多关系
- `@JoinTable` - 定义中间表
  - `name = "student_course"` - 中间表名
  - `joinColumns` - 当前实体的外键（student_id）
  - `inverseJoinColumns` - 关联实体的外键（course_id）
- 通常在"主控方"（Student）定义 `@JoinTable`，"被控方"（Course）使用 `mappedBy`

### 步骤1：创建实体类

#### 创建 Student 实体

```java
package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * 学生实体
 */
@Data
@Entity
@Table(name = "student")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学生姓名不能为空")
    @Column(nullable = false, length = 32)
    private String studentName;

    private String studentNo;

    // 多对多关联：课程列表
    // 使用 @JsonIgnore 避免JSON序列化无限递归
    // 1. 当返回实体时，不会包含 courses 字段，避免序列化问题
    // 2. 如果需要返回关联数据，应使用 DTO（Data Transfer Object）
    // 3. 配合 @EqualsAndHashCode(of = "id") 避免集合字段导致 equals/hashCode 问题
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_course",  // 中间表名
        joinColumns = @JoinColumn(name = "student_id"),  // 当前实体在中间表的外键
        inverseJoinColumns = @JoinColumn(name = "course_id")  // 关联实体在中间表的外键
    )
    private Set<Course> courses = new HashSet<>();
}
```

#### 创建 Course 实体

```java
package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * 课程实体
 */
@Data
@Entity
@Table(name = "course")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "课程名称不能为空")
    @Column(nullable = false, length = 64)
    private String courseName;

    private Integer credit;

    // 多对多关联：学生列表
    // 使用 @JsonIgnore 避免JSON序列化无限递归
    // 1. 当返回实体时，不会包含 students 字段，避免序列化问题
    // 2. 如果需要返回关联数据，应使用 DTO（Data Transfer Object）
    // 3. 配合 @EqualsAndHashCode(of = "id") 避免集合字段导致 equals/hashCode 问题
    @JsonIgnore
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();
}
```

**注解说明**：

- `@ManyToMany` - 多对多关联
- `@JoinTable` - 定义中间表
  - `name = "student_course"` - 中间表名
  - `joinColumns` - 当前实体的外键
  - `inverseJoinColumns` - 关联实体的外键

- `mappedBy` - 关系的维护端
  - 在 Course 中使用 `mappedBy = "courses"`
  - 表示由 Student 的 courses 属性维护关系

---

### 步骤2：创建 Repository

#### StudentRepository

```java
package com.example.jpaadvanceddemo.repository;

import com.example.jpaadvanceddemo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // 查询学生及其课程（使用 JOIN FETCH 避免N+1问题）
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id")
    Optional<Student> findByIdWithCourses(@Param("id") Long id);

    // 查询所有学生及其课程
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses")
    List<Student> findAllWithCourses();
}
```

### JPQL 查询理论讲解（多对多）

**多对多关系的 N+1 问题**

多对多关系的 N+1 问题更加复杂，因为涉及**三张表**的关联。

```java
// ❌ 错误方式：会产生严重的N+1问题
List<Student> students = studentRepository.findAll();
for (Student student : students) {
    // 每个学生访问课程列表时，都会触发额外的SQL查询
    Set<Course> courses = student.getCourses();
}
```

假设有10个学生，每个学生平均选了5门课：
- 第1条SQL：查询所有学生
- 第2-11条SQL：为每个学生查询课程列表（10次额外查询）
- **总计11条SQL**

**多对多的SQL执行过程（错误方式）**：

```sql
-- 第1条：查询所有学生
SELECT * FROM student;

-- 第2条：查询学生1的课程（需要JOIN中间表）
SELECT c.*
FROM course c
JOIN student_course sc ON c.id = sc.course_id
WHERE sc.student_id = 1;

-- 第3条：查询学生2的课程
SELECT c.*
FROM course c
JOIN student_course sc ON c.id = sc.course_id
WHERE sc.student_id = 2;

-- ... 以此类推
```

**解决方案：使用 JOIN FETCH**

```java
@Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id")
Optional<Student> findByIdWithCourses(@Param("id") Long id);
```

**JPQL语句解析**：

```java
"SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id"
```

| 关键词 | 说明 |
|--------|------|
| `SELECT s` | 选择 Student 对象 |
| `FROM Student s` | 从 Student 实体查询 |
| `LEFT JOIN FETCH` | 左外连接 + 立即加载 |
| `s.courses` | 关联的集合属性 |
| `WHERE s.id = :id` | 条件过滤 |

**生成的SQL（涉及三张表）**：

```sql
-- 只需1条SQL，同时查询学生、课程和中间表
SELECT s.*, c.*
FROM student s
LEFT OUTER JOIN student_course sc ON s.id = sc.student_id
LEFT OUTER JOIN course c ON sc.course_id = c.id
WHERE s.id = ?;
```

**为什么多对多也必须用 DISTINCT？**

多对多关系中，如果一个学生选了多门课，SQL结果集会有多行：

| s.id | s.student_name | c.id | c.course_name |
|------|----------------|------|---------------|
| 1 | 张三 | 101 | Java |
| 1 | 张三 | 102 | Python |
| 1 | 张三 | 103 | MySQL |

SQL返回了3行，但我们需要的是 **1个Student对象**（包含3个Course）。

**双向多对多的查询优化**

如果需要查询课程及其选课学生：

```java
// CourseRepository
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = :id")
    Optional<Course> findByIdWithStudents(@Param("id") Long id);
}
```

JPQL语句：
```java
"SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = :id"
```

生成的SQL：
```sql
SELECT c.*, s.*
FROM course c
LEFT OUTER JOIN student_course sc ON c.id = sc.course_id
LEFT OUTER JOIN student s ON sc.student_id = s.id
WHERE c.id = ?;
```

**多对多查询的最佳实践**：

1. **单向查询即可**：通常只需要从一方查询（如查询学生及其课程）
2. **使用 DISTINCT**：避免主对象重复
3. **考虑分页**：多对多结果集可能很大，建议分页查询
4. **避免双向FETCH**：不要同时 `FETCH s.courses` 和 `c.students`，会导致笛卡尔积

```java
// ❌ 避免：双向同时FETCH
@Query("SELECT s FROM Student s " +
       "LEFT JOIN FETCH s.courses c " +
       "LEFT JOIN FETCH c.students")  // 会导致问题！
List<Student> findAll();
```

---

### 步骤3：创建 DTO（数据传输对象）

多对多关系中，我们需要创建 DTO 来返回包含关联数据的对象。

#### 创建 StudentWithCoursesDTO

创建 `src/main/java/com/example/jpaadvanceddemo/dto/StudentWithCoursesDTO.java`：

```java
package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 学生 DTO（包含课程列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentWithCoursesDTO {

    private Long id;
    private String studentName;
    private String studentNo;
    private Set<Course> courses;
}
```

#### 创建 CourseWithStudentsDTO

创建 `src/main/java/com/example/jpaadvanceddemo/dto/CourseWithStudentsDTO.java`：

```java
package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 课程 DTO（包含学生列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseWithStudentsDTO {

    private Long id;
    private String courseName;
    private Integer credit;
    private Set<Student> students;
}
```

---

### 步骤4：创建 Service

#### StudentService 接口

创建 `src/main/java/com/example/jpaadvanceddemo/service/StudentService.java`：

```java
package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.entity.Student;

import java.util.List;

public interface StudentService {

    Student createStudent(Student student);
    Student getStudentById(Long id);
    Student getStudentByIdWithCourses(Long id);
    List<Student> getAllStudents();
    List<Student> getAllStudentsWithCourses();
    Student addCourseToStudent(Long studentId, Long courseId);
    Student updateStudent(Student student);
    void deleteStudent(Long id);
}
```

#### CourseService 接口

创建 `src/main/java/com/example/jpaadvanceddemo/service/CourseService.java`：

```java
package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.entity.Course;

import java.util.List;

public interface CourseService {

    Course createCourse(Course course);
    Course getCourseById(Long id);
    Course getCourseByIdWithStudents(Long id);
    List<Course> getAllCourses();
    List<Course> getAllCoursesWithStudents();
    Course updateCourse(Course course);
    void deleteCourse(Long id);
}
```

#### StudentServiceImpl 实现类

创建 `src/main/java/com/example/jpaadvanceddemo/service/impl/StudentServiceImpl.java`：

```java
package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.entity.Student;
import com.example.jpaadvanceddemo.repository.CourseRepository;
import com.example.jpaadvanceddemo.repository.StudentRepository;
import com.example.jpaadvanceddemo.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    @Override
    public Student getStudentByIdWithCourses(Long id) {
        return studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getAllStudentsWithCourses() {
        return studentRepository.findAllWithCourses();
    }

    @Override
    @Transactional
    public Student addCourseToStudent(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        student.getCourses().add(course);
        course.getStudents().add(student);

        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
```

#### CourseServiceImpl 实现类

创建 `src/main/java/com/example/jpaadvanceddemo/service/impl/CourseServiceImpl.java`：

```java
package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.repository.CourseRepository;
import com.example.jpaadvanceddemo.service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    @Override
    public Course getCourseByIdWithStudents(Long id) {
        return courseRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getAllCoursesWithStudents() {
        return courseRepository.findAllWithStudents();
    }

    @Override
    @Transactional
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
```

---

### 步骤5：创建 Controller

#### StudentController

创建 `src/main/java/com/example/jpaadvanceddemo/controller/StudentController.java`：

```java
package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.dto.StudentWithCoursesDTO;
import com.example.jpaadvanceddemo.entity.Student;
import com.example.jpaadvanceddemo.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学生管理", description = "学生相关接口")
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "创建学生")
    public Student createStudent(@Valid @RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询学生")
    public Student getStudentById(
            @Parameter(description = "学生ID") @PathVariable Long id
    ) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/{id}/with-courses")
    @Operation(summary = "查询学生及其课程")
    public StudentWithCoursesDTO getStudentByIdWithCourses(
            @Parameter(description = "学生ID") @PathVariable Long id
    ) {
        return studentService.getStudentByIdWithCourses(id);
    }

    @GetMapping
    @Operation(summary = "查询所有学生")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/all-with-courses")
    @Operation(summary = "查询所有学生及其课程")
    public List<StudentWithCoursesDTO> getAllStudentsWithCourses() {
        return studentService.getAllStudentsWithCourses();
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "为学生添加课程")
    public Student addCourseToStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "课程ID") @PathVariable Long courseId
    ) {
        return studentService.addCourseToStudent(studentId, courseId);
    }

    @PutMapping
    @Operation(summary = "更新学生")
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public void deleteStudent(
            @Parameter(description = "学生ID") @PathVariable Long id
    ) {
        studentService.deleteStudent(id);
    }
}
```

#### CourseController

创建 `src/main/java/com/example/jpaadvanceddemo/controller/CourseController.java`：

```java
package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.dto.CourseWithStudentsDTO;
import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "课程管理", description = "课程相关接口")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "创建课程")
    public Course createCourse(@Valid @RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询课程")
    public Course getCourseById(
            @Parameter(description = "课程ID") @PathVariable Long id
    ) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/{id}/with-students")
    @Operation(summary = "查询课程及其学生")
    public CourseWithStudentsDTO getCourseByIdWithStudents(
            @Parameter(description = "课程ID") @PathVariable Long id
    ) {
        return courseService.getCourseByIdWithStudents(id);
    }

    @GetMapping
    @Operation(summary = "查询所有课程")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/all-with-students")
    @Operation(summary = "查询所有课程及其学生")
    public List<CourseWithStudentsDTO> getAllCoursesWithStudents() {
        return courseService.getAllCoursesWithStudents();
    }

    @PutMapping
    @Operation(summary = "更新课程")
    public Course updateCourse(@RequestBody Course course) {
        return courseService.updateCourse(course);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public void deleteCourse(
            @Parameter(description = "课程ID") @PathVariable Long id
    ) {
        courseService.deleteCourse(id);
    }
}
```

---

### 步骤6：扩展数据初始化器

修改 `DataInitializer.java`，添加学生和课程的初始化数据：

```java
@Override
@Transactional
public void run(String... args) throws Exception {
    if (departmentRepository.count() > 0) {
        return;
    }

    initDepartmentAndEmployee();
    initStudentAndCourse();  // 添加这行
}

private void initStudentAndCourse() {
    // 创建课程
    Course course1 = new Course();
    course1.setCourseName("Java程序设计");
    course1.setCredit(4);

    Course course2 = new Course();
    course2.setCourseName("Python数据分析");
    course2.setCredit(3);

    Course course3 = new Course();
    course3.setCourseName("MySQL数据库");
    course3.setCredit(2);

    courseRepository.save(course1);
    courseRepository.save(course2);
    courseRepository.save(course3);

    // 创建学生并选课
    Student student1 = new Student();
    student1.setStudentName("张小明");
    student1.setStudentNo("2021001");
    student1.getCourses().add(course1);
    student1.getCourses().add(course3);

    Student student2 = new Student();
    student2.setStudentName("李小红");
    student2.setStudentNo("2021002");
    student2.getCourses().add(course1);
    student2.getCourses().add(course2);

    studentRepository.save(student1);
    studentRepository.save(student2);
}
```

---

### 步骤7：测试多对多关联

使用 Swagger UI 测试：http://localhost:8080/swagger-ui/index.html

#### 测试1：创建课程

**POST** `/api/courses`

```json
{
  "courseName": "数据结构与算法",
  "credit": 4
}
```

#### 测试2：创建学生

**POST** `/api/students`

```json
{
  "studentName": "王小华",
  "studentNo": "2021003"
}
```

#### 测试3：为学生添加课程

**POST** `/api/students/1/courses/1`

将学生1和课程1关联起来。

#### 测试4：查询学生及其课程（使用 JOIN FETCH）

**GET** `/api/students/1/with-courses`

返回的学生对象中包含完整的 `courses` 集合。

观察 H2 控制台，应该只会看到 **1 条 SQL**（三表 LEFT JOIN）：
```sql
SELECT s.*, c.*
FROM student s
LEFT OUTER JOIN student_course sc ON s.id = sc.student_id
LEFT OUTER JOIN course c ON sc.course_id = c.id
WHERE s.id = 1;
```

#### 测试5：查询课程及其学生

**GET** `/api/courses/1/with-students`

查看哪些学生选了这门课。

#### 测试6：验证中间表数据

访问 H2 控制台：http://localhost:8080/h2-console

执行 SQL 查询中间表：
```sql
SELECT * FROM student_course;
```

应该看到类似结果：
| student_id | course_id |
|------------|-----------|
| 1 | 1 |
| 1 | 3 |
| 2 | 1 |
| 2 | 2 |

---

## 第五部分：JPA 审计功能（15分钟）

### 什么是审计功能？

JPA 审计功能可以自动记录实体的创建时间、修改时间、创建人、修改人等信息，避免手动维护这些字段。

### 示例1：自动填充创建时间、修改时间

#### 在实体类上添加注解

```java
package com.example.jpaadvanceddemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户实体（带审计功能）
 */
@Data
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    // 审计字段：自动记录创建时间
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    // 审计字段：自动记录修改时间
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
```

#### 启用审计功能

在配置类中添加 `@EnableJpaAuditing` 注解：

```java
package com.example.jpaadvanceddemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // 启用JPA审计功能
public class JpaConfig {
}
```

#### 测试审计功能

```java
@SpringBootTest
class AuditingTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAuditing() {
        // 创建用户
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@example.com");

        User saved = userRepository.save(user);

        // createTime 和 updateTime 会自动填充
        System.out.println("创建时间: " + saved.getCreateTime());
        System.out.println("修改时间: " + saved.getUpdateTime());

        // 修改用户
        saved.setEmail("newemail@example.com");
        User updated = userRepository.save(saved);

        // updateTime 会自动更新
        System.out.println("新的修改时间: " + updated.getUpdateTime());
    }
}
```

### 示例2：自动记录创建人、修改人

#### 添加审计字段

```java
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {

    // ... 其他字段 ...

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    // ... 审计时间字段 ...
}
```

#### 配置审计感知

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        // 返回当前操作用户
        // 实际项目中应该从 Spring Security 或 Session 中获取当前用户
        return () -> Optional.of("admin");
    }
}
```

### 审计功能总结

| 注解 | 作用 | 自动填充时机 |
|------|------|-------------|
| `@CreatedDate` | 创建时间 | 首次保存时 |
| `@LastModifiedDate` | 修改时间 | 每次保存时 |
| `@CreatedBy` | 创建人 | 首次保存时 |
| `@LastModifiedBy` | 修改人 | 每次保存时 |

---

## 第六部分：实战项目练习（剩余时间）

### 项目背景

开发一个**图书管理系统**，实现以下功能：

- 图书与作者：多对多关系
- 图书与分类：多对一关系
- 图书与借阅记录：一对多关系

### 任务要求

#### 1. 数据库设计（10分钟）

设计以下实体：

**Book（图书）**
- id, title, ISBN, publishDate, price
- category（多对一）
- authors（多对多）
- borrowRecords（一对多）

**Category（分类）**
- id, name, description
- books（一对多）

**Author（作者）**
- id, name, nationality
- books（多对多）

**BorrowRecord（借阅记录）**
- id, book, borrower, borrowDate, returnDate
- book（多对一）

#### 2. 实体创建（20分钟）

创建以上4个实体类，正确配置关联关系。

#### 3. Repository 层（10分钟）

创建对应的 Repository 接口，包含：
- 基础 CRUD
- 关联查询（使用 JOIN FETCH）
- 自定义 JPQL 查询（至少2个）

#### 4. Service 层（15分钟）

实现业务逻辑：
- 借书功能
- 还书功能
- 查询某本书的所有借阅记录

#### 5. Controller 层（10分钟）

创建 REST API：
- POST /api/books/{bookId}/borrow - 借书
- POST /api/books/{bookId}/return - 还书
- GET /api/books/{bookId}/borrow-records - 查询借阅记录

#### 6. DTO 创建（10分钟）

为关联查询创建 DTO，避免无限递归。

#### 7. 测试（剩余时间）

使用 Swagger UI 或 Postman 测试所有功能。

### 提示

- 注意级联保存和删除
- 使用 DTO 避免序列化问题
- 使用 `@Transactional` 确保事务一致性
- 使用审计功能自动记录时间

---

## 课后作业

### 必做题（基础）

1. **一对一关联实践**
   - 完成用户和用户详情的完整 CRUD
   - 测试级联保存和删除
   - 使用 DTO 避免序列化问题

2. **一对多关联实践**
   - 完成部门和员工的 CRUD
   - 使用 JOIN FETCH 避免 N+1 问题
   - 创建 DepartmentWithEmployeesDTO

3. **多对多关联实践**
   - 完成学生和课程的选课功能
   - 测试中间表自动维护
   - 创建双向关联查询的 DTO

4. **审计功能实践**
   - 为所有实体添加审计字段（createTime、updateTime）
   - 测试审计功能自动填充
   - 验证修改时间自动更新

---

### 选做题（进阶）

1. **图书管理系统**
   - 完成课堂上的图书管理系统实战项目
   - 实现借书、还书功能
   - 添加审计功能记录借阅时间

2. **JPQL 查询练习**
   - 编写至少 3 个自定义 JPQL 查询
   - 包含条件查询、聚合查询
   - 测试查询结果正确性

3. **关联查询优化**
   - 找出项目中的 N+1 问题
   - 使用 JOIN FETCH 优化
   - 对比优化前后的 SQL 执行次数

---

### 挑战题（额外）

1. **完整 REST API**
   - 为所有实体创建完整的 REST API
   - 实现分页和排序功能
   - 添加参数校验和异常处理

2. **数据校验**
   - 使用 `@Valid` 进行请求参数校验
   - 自定义校验注解
   - 处理校验异常

---

## 常见问题解答

### Q1：懒加载异常 (LazyInitializationException)

**原因**：在 EntityManager 关闭后访问懒加载属性。

**解决方案**：
```java
// 方式1：使用 JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.userProfile WHERE u.id = :id")
User findByIdWithProfile(@Param("id") Long id);

// 方式2：使用 @EntityGraph
@EntityGraph(attributePaths = {"userProfile"})
Optional<User> findById(Long id);

// 方式3：配置 spring.jpa.open-in-view=true（不推荐）
```

---

### Q2：N+1 问题

**原因**：查询父实体列表后，访问每个父实体的关联实体，导致 N 次额外查询。

**解决方案**：
```java
// 使用 JOIN FETCH 一次查询
@Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

---

### Q3：级联删除异常

**原因**：子实体有关联数据，无法直接删除。

**解决方案**：
```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<UserProfile> userProfiles;
```

---

### Q4：无限递归 JSON 序列化

**原因**：双向关联导致无限递归。

**解决方案**：
```java
@Data
@Entity
public class User {
    @OneToOne(mappedBy = "user")
    @JsonManagedReference  // 主引用
    private UserProfile userProfile;
}

@Data
@Entity
public class UserProfile {
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference  // 反向引用
    private User user;
}
```

---

## 扩展阅读

1. **Spring Data JPA 官方文档**
   - [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

2. **Hibernate 官方文档**
   - [Hibernate ORM](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/)

3. **Jakarta Persistence 规范**
   - [Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/)

---

## 下周预告

**第6周：Spring Boot 事务管理与缓存**

1. **事务管理**
   - 编程式事务 vs 声明式事务
   - @Transactional 注解详解
   - 事务传播行为
   - 事务隔离级别
   - 事务失效场景

2. **Spring Cache 抽象**
   - @Cacheable、@CachePut、@CacheEvict
   - 缓存注解参数详解
   - 多级缓存配置

3. **集成 Redis**
   - Redis 安装与配置
   - RedisTemplate 使用
   - 序列化问题解决

4. **分布式锁**
   - Redisson 框架
   - 分布式锁实现

---

## 教学反思点

课后请思考：

1. JPA 三种关联关系（一对一、一对多、多对多）的使用场景？
2. 如何避免N+1查询问题？
3. DTO 模式在解决序列化问题中的作用？
4. 懒加载和急加载（EAGER）的选择策略？
5. JPA 审计功能在实际项目中的应用价值？

---

**文档版本**：v2.0 (精简版)
**最后更新**：2025-02-03
**适用版本**：Spring Boot 3.5.10、JDK 21、Spring Data JPA
**主要内容**：JPA关联关系、DTO模式、审计功能、实战练习

