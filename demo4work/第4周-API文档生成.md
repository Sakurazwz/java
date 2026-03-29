# 第4周：API 文档生成（SpringDoc Swagger-UI）

## 课程目标

通过本周课程，学生将学会：
1. 理解为什么需要 API 文档
2. 了解 OpenAPI 3.0 规范
3. 掌握 SpringDoc 的整合步骤
4. 学会使用 Swagger 注解编写接口文档
5. 实现接口分组管理
6. 掌握在线调试功能
7. 了解接口文档最佳实践

---

## 课前准备

### 确认环境

确保前三周的项目可以正常运行：
- 第1周的 DevTools 和多环境配置正常
- 第2周的统一响应和异常处理正常
- 第3周的日志切面和跨域配置正常

### 本周需要的依赖

**SpringDoc** 是 Spring Boot 官方推荐的 OpenAPI 3.0 规范实现，用于生成 API 文档。

```xml
<!-- SpringDoc OpenAPI 3.0（Swagger UI） -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.0</version>
</dependency>
```

**注意**：
- Spring Boot 3.x 使用 Jakarta EE，SpringDoc 已完全兼容
- 此依赖已包含 Swagger UI，无需额外配置
- 推荐版本组合：**Spring Boot 3.5.10 + SpringDoc 2.8.0**（最新稳定版本，支持 @Slf4j）
- 版本对应关系：
  - Spring Boot 3.2.x → SpringDoc 2.3.0
  - Spring Boot 3.3.x → SpringDoc 2.4.0
  - Spring Boot 3.4.x → SpringDoc 2.5.0/2.6.0
  - Spring Boot 3.5.7+ → SpringDoc 2.8.0 ✅ 推荐（包含 3.5.10）

---

## 第一部分：为什么需要 API 文档（10分钟）

### 问题场景

在前后端分离开发中，前端开发人员需要知道：

1. **接口地址**：`/user/list`、`/user/register`
2. **请求方法**：GET、POST、PUT、DELETE
3. **请求参数**：参数名、类型、是否必填、格式
4. **返回格式**：返回字段说明、数据类型
5. **错误码**：各种错误码的含义

#### 传统方式（低效）

**方式1：Word 文档**
```
1. 用户注册接口
   - 请求地址：/user/register
   - 请求方法：POST
   - 请求参数：
     - username: 用户名，字符串，必填
     - password: 密码，字符串，必填
     - email: 邮箱，字符串，必填
     ...
```

**问题**：
- 维护困难，容易过时
- 不直观，无法测试
- 版本管理混乱

---

**方式2：口头沟通**
- 前端：请问注册接口的参数是什么？
- 后端：用户名、密码、邮箱...
- 前端：密码有什么要求吗？
- 后端：6-20位...
- 前端：返回什么格式？
- 后端：统一的 Result 结构...

**问题**：
- 沟通成本高
- 容易遗漏信息
- 不利于知识沉淀

---

### API 文档的优势

1. **自动生成**：代码和文档同步，无需额外维护
2. **在线调试**：直接在浏览器测试接口
3. **清晰直观**：参数说明、示例一目了然
4. **版本管理**：随代码版本管理
5. **团队协作**：前后端统一文档

---

### 主流 API 文档工具对比

| 工具 | 类型 | 优点 | 缺点 | 推荐度 |
|------|------|------|------|--------|
| **SpringDoc** | 官方 | Spring Boot 原生支持，配置简单 | UI 较简陋 | ⭐⭐⭐⭐⭐ |
| **Swagger UI** | 官方 | 功能完整，标准实现 | 较丑 | ⭐⭐⭐⭐⭐ |
| **Knife4j** | 第三方 | UI 美观，功能增强 | 版本兼容性问题 | ⭐⭐⭐ |
| **YApi** | 第三方 | 功能强大，国内流行 | 需要单独部署 | ⭐⭐⭐⭐ |

**本课程选择**：**SpringDoc + Swagger UI**（官方推荐，最稳定）

---

## 第二部分：添加依赖和配置（15分钟）

### 步骤1：添加 SpringDoc 依赖

在 `pom.xml` 中添加：

```xml
<dependencies>
    <!-- SpringDoc OpenAPI 3.0（Swagger UI） -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.0</version>
    </dependency>
</dependencies>
```

---

### 步骤2：配置 application.yml

在 `application.yml` 中添加配置：

```yaml
springdoc:
  # API 文档基本信息
  api-docs:
    # API 文档访问路径
    path: /v3/api-docs
  # 分组配置
  group-configs:
    # 按标签分组
    group-by: tags
    # 按包分组
    # package-configs: com.example.demo.controller
  # Swagger UI 配置
  swagger-ui:
    # Swagger UI 访问路径
    path: /swagger-ui.html
    # 默认展开的标签
    default-models-expand-depth: 2
    # 默认展开的级别
    default-model-expand-depth: 2
    # 显示请求时长
    display-request-duration: true
    # 按字母顺序排序
    operations-sorter: alpha
    # 标签排序器
    tags-sorter: alpha
    # 是否启用
    enabled: true

# 应用信息（用于文档显示）
app:
  name: ${spring.application.name}
  version: 1.0.0
  description: Spring Boot API 文档示例
```

---

### 步骤3：配置基本信息

创建 `com.example.demo.config.OpenApiConfig.java`：

```java
package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置 API 文档基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 文档基本信息
                .info(new Info()
                        .title("Spring Boot API 文档")
                        .description("基于 Spring Boot 3.5.10 + SpringDoc 的 API 文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("你的名字")
                                .email("your-email@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
```

---

### 步骤4：测试文档访问

启动应用后，访问以下地址：

- **Swagger UI**：`http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**：`http://localhost:8080/v3/api-docs`

您应该能看到 Swagger UI 界面，显示了所有的 API 接口。

---

## 第三部分：使用 Swagger 注解（30分钟）

### 常用注解说明

#### 1. 类级别注解

| 注解 | 说明 | 示例 |
|------|------|------|
| **@Tag** | Controller 分组标签 | `@Tag(name = "用户管理", description = "用户相关接口")` |
| **@Tags** | 多个标签 | `@Tags({"用户", "管理员"})` |

#### 2. 方法级别注解

| 注解 | 说明 | 示例 |
|------|------|------|
| **@Operation** | 接口说明 | `@Operation(summary = "用户注册", description = "新用户注册")` |
| **@ApiResponses** | 响应说明 | 见下方详细说明 |

#### 3. 参数注解

| 注解 | 说明 | 示例 |
|------|------|------|
| **@Parameter** | 单个参数说明 | `@Parameter(description = "用户名", required = true)` |
| **@Parameters** | 多个参数说明 | 见下方详细说明 |
| **@RequestBody** | 请求体参数 | `@Schema(description = "用户信息")` |
| **@RequestParam** | 路径参数 | `@Parameter(description = "用户ID")` |
| **@PathVariable** | 路径变量 | `@Parameter(description = "订单ID")` |

#### 4. 实体注解

| 注解 | 说明 | 示例 |
|------|------|------|
| **@Schema** | 实体属性说明 | `@Schema(description = "用户名", example = "张三")` |

---

### 为 UserController 添加文档注解

**注意**：在创建 UserController 之前，需要先创建 `UserService`。由于第4周还没有涉及数据库（MyBatis-Plus 是第5周的内容），这里提供一个简单的内存存储版本的 `UserService`。

创建 `com.example.demo.service.UserService.java`：

```java
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
```

**说明**：
- 这个 `UserService` 使用内存存储（`ConcurrentHashMap`）
- 预置了 3 条测试数据（zhangsan、lisi、wangwu）
- 第5周学习 MyBatis-Plus 后，会替换为真实的数据库实现

---

现在创建 `com.example.demo.controller.UserController.java`：

```java
package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户的增删改查接口")
public class UserController {

    private final UserService userService;

    /**
     * 查询单个用户
     */
    @GetMapping("/get")
    @Operation(summary = "查询单个用户", description = "根据用户ID查询用户信息")
    @Parameters({
        @Parameter(name = "id", description = "用户ID", required = true, example = "1",
                in = ParameterIn.QUERY)
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    public Result<User> getUser(
            @Parameter(description = "用户ID", required = true) @RequestParam Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", description = "查询所有用户信息，支持分页")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public Result<List<User>> getUserList() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRegisterDTO.class)
            )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "参数校验失败"),
        @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    public Result<String> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success("注册成功");
    }
}
```

---

### 为 DTO 添加注解

修改 `com.example.demo.dto.UserRegisterDTO.java`：

```java
package com.example.demo.dto;

import com.example.demo.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户注册 DTO
 */
@Data
@Schema(description = "用户注册请求参数")
public class UserRegisterDTO {

    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3, maxLength = 20)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @Schema(description = "密码", example = "Password123", requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$")
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$",
             message = "密码必须包含大小写字母和数字，长度8-20位")
    private String password;

    @Schema(description = "确认密码", example = "Password123")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Schema(description = "邮箱", example = "zhangsan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "年龄", example = "20")
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 120, message = "年龄必须小于120")
    private Integer age;

    @Schema(description = "性别", example = "MALE")
    private Gender gender;
}
```

**注意**：`Gender` 枚举已定义在 `com.example.demo.enums` 包中，这里直接使用。

如果 Gender 枚举还没有 `@Schema` 注解，建议添加：

```java
package com.example.demo.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 性别枚举
 */
@Schema(description = "性别枚举")
public enum Gender {
    @Schema(description = "男")
    MALE("男", 1),

    @Schema(description = "女")
    FEMALE("女", 0),

    @Schema(description = "其他")
    OTHER("其他", 2);

    private final String description;
    private final Integer code;

    Gender(String description, Integer code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }
}
```

---

## 第四部分：测试文档功能（15分钟）

### 步骤1：启动应用

```bash
mvn spring-boot:run
```

### 步骤2：访问 Swagger UI

打开浏览器访问：
```
http://localhost:8080/swagger-ui.html
```

---

### 步骤3：查看接口文档

您应该能看到：
1. **用户管理** 分组
2. 三个接口：
   - `GET /user/get` - 查询单个用户
   - `GET /user/list` - 查询用户列表
   - `POST /user/register` - 用户注册

---

### 步骤4：在线调试接口

#### 测试1：查询用户列表

1. 点击 `GET /user/list` 接口
2. 输入ID的值，比如1，点击 `Try it out` 按钮
3. 点击 `Execute` 按钮
4. 查看响应结果

#### 测试2：用户注册（带参数校验）

1. 点击 `POST /user/register` 接口
2. 点击 `Try it out`
3. 在 Request body 中输入测试数据：

```json
{
  "username": "zhaoliu",
  "password": "Password123",
  "confirmPassword": "Password123",
  "email": "zhaoliu@example.com",
  "phone": "13800138000",
  "age": 20,
  "gender": "MALE"
}
```

4. 点击 `Execute`
5. 查看响应结果

#### 测试3：参数校验错误

故意输入错误数据：

```json
{
  "username": "ab",
  "password": "123",
  "email": "invalid-email"
}
```

您应该能看到：
- **400 Bad Request**
- 响应体中包含详细的错误信息

---

## 第五部分：高级功能（可选）

### 功能1：接口分组

使用 `@Tag` 对接口进行分组：

```java
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    // ...
}

@RestController
@RequestMapping("/order")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {
    // ...
}
```

---

### 功能2：响应示例

在 `@Operation` 中添加响应示例：

```java
@GetMapping("/get")
@Operation(
    summary = "查询单个用户",
    description = "根据用户ID查询用户信息",
    responses = {
        @ApiResponse(
            responseCode = "200",
            description = "查询成功",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        )
    }
)
public Result<User> getUser(@RequestParam Long id) {
    // ...
}
```

---

### 功能3：安全配置（JWT）

如果需要在 API 文档中添加 JWT 认证功能，按以下步骤操作：

#### 步骤1：修改 OpenApiConfig 配置

在 `OpenApiConfig.java` 的 `customOpenAPI()` 方法中添加 JWT 配置：

```java
package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 文档基本信息
                .info(new Info()
                        .title("Spring Boot API 文档")
                        .description("基于 Spring Boot 3.5.10 + SpringDoc 的 API 文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("你的名字")
                                .email("your-email@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                // JWT 认证配置
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
```

**配置说明**：
- `.addSecurityItem()` - 声明全局安全需求（所有接口默认需要认证）
- `.addSecuritySchemes()` - 定义认证方案（bearer-key 为方案名称）
- `type(SecurityScheme.Type.HTTP)` - 使用 HTTP 认证
- `scheme("bearer")` - Bearer Token 认证
- `bearerFormat("JWT")` - Token 格式为 JWT

#### 步骤2：重启应用

重启应用后，访问 Swagger UI：`http://localhost:8080/swagger-ui.html`

您会看到：
- 页面顶部出现 🔣 **Authorize** 按钮
- 每个接口旁边显示小锁图标 🔒

#### 步骤3：获取测试 Token

**方式1：使用在线工具生成（推荐用于测试）**

1. 访问 **https://jwt.io/**
2. 可以直接使用以下测试 Token：
   ```
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
   ```
3. 这个 Token 是公开的测试用 Token，包含：
   - 用户ID：1234567890
   - 用户名：John Doe
   - 签名：已签名（确保完整性）

**方式2：通过登录接口获取（生产环境）**

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginRequest request) {
        // 1. 验证用户名密码
        // 2. 生成 JWT Token
        String token = generateToken(request.getUsername());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("type", "Bearer");

        return Result.success(data);
    }

    private String generateToken(String username) {
        // 实际应使用 JWT 库（如 io.jsonwebtoken:jjwt）
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
               "eyJ1c2VybmFtZSI6Ii" + username + "In0." +
               "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    }
}
```

#### 步骤4：在 Swagger UI 中使用 Token

1. 点击页面顶部的 🔣 **Authorize** 按钮
2. 在弹出框的输入框中输入 Token（**不要**包含 `Bearer ` 前缀）：
   ```
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
   ```
3. 点击 **Authorize** 按钮
4. 点击 **Close** 关闭弹窗
5. 之后所有接口请求会自动在请求头中携带：
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

#### 步骤5：验证 Token 是否生效

1. 展开任意接口，点击 **Try it out**
2. 执行请求
3. 查看 **Request headers** 部分，应该能看到：
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

#### 重要提示

**当前配置的作用**：
- ✅ 在 Swagger UI 中添加 Token 输入功能
- ✅ 所有请求自动携带 `Authorization: Bearer <token>` 请求头

**后端验证（需要额外实现）**：
- ❌ 当前配置**不会**验证 Token 的有效性
- ❌ 需要自己实现拦截器或过滤器来验证 Token
- 可以使用 `io.jsonwebtoken:jjwt` 库来验证和解析 Token

**JWT 完整实现（后续课程）**：
- Token 生成和验证
- 拦截器/过滤器实现
- 注解方式的接口保护
- Token 过期和刷新

---

## 课后作业

### 必做题（基础）

1. **整合 SpringDoc**
   - 添加 SpringDoc 依赖
   - 配置 `application.yml`
   - 创建 `OpenApiConfig` 配置类
   - 访问 `http://localhost:8080/swagger-ui.html` 验证

2. **完善 UserController 文档**
   - 添加 `@Tag` 注解
   - 为所有方法添加 `@Operation` 注解
   - 为所有参数添加 `@Parameter` 注解
   - 完善 `UserRegisterDTO` 的 `@Schema` 注解

3. **在线调试**
   - 使用 Swagger UI 调试所有接口
   - 验证参数校验是否生效
   - 测试异常场景

---

### 选做题（进阶）

1. **创建新的 Controller**
   - 创建 `OrderController`（订单管理）
   - 实现 CRUD 接口
   - 添加完整的文档注解
   - 创建独立的接口分组

2. **响应码文档**
   - 在文档中添加响应码说明
   - 使用 `@ApiResponses` 注解
   - 为每个错误码提供说明和示例

3. **全局参数配置**
   - 配置 JWT Token 认证
   - 在文档页面添加 Token
   - 测试需要认证的接口

---

### 挑战题（额外）

1. **枚举类型文档**
   - 创建性别枚举
   - 在 `@Schema` 中添加 `allowableValues`
   - 测试枚举参数

2. **文件上传文档**
   - 为文件上传接口添加文档
   - 说明文件大小和类型限制
   - 提供上传示例

3. **分组管理**
   - 创建多个分组
   - 使用 `@Tag` 和 `@Operation` 组织
   - 实现清晰的文档结构

---

## 常见问题

### 问题1：页面无法访问（404）

**解决方案**：
1. 检查依赖是否正确添加
2. 确认应用正常启动
3. 访问正确的地址：`/swagger-ui.html`

---

### 问题2：接口不显示

**可能原因**：
1. Controller 没有 `@RequestMapping` 或 `@RestController`
2. 接口没有被扫描到（包路径配置问题）
3. Swagger 配置的路径不正确

**解决方案**：
1. 确保 Controller 有正确的注解
2. 检查 `springdoc.packages-to-scan` 配置
3. 查看启动日志，确认扫描的包路径

---

### 问题3：参数描述不显示

**解决方案**：
- 使用 `@Parameter` 注解（对于简单参数）
- 使用 `@Schema` 注解（对于实体属性）
- 确保 `description` 属性已填写

---

## 下周预告

**第5周：MyBatis-Plus 代码生成**

- MyBatis-Plus 简介
- 配置数据源
- 代码生成器使用
- CRUD 操作实现
- 分页查询

---

**文档版本**：v2.3（使用 Spring Boot 3.5.10 + SpringDoc 2.8.0）
**适用版本**：Spring Boot 3.5.10、JDK 21

**版本说明**：
- 本课程使用 Spring Boot 3.5.10 + SpringDoc 2.8.0，最新稳定版本且支持 Lombok @Slf4j
- **重要**：SpringDoc 2.7.0 及以下版本不兼容 Spring Boot 3.5.x，必须使用 2.8.0 或更高版本
- 如需使用其他 Spring Boot 版本，请参考版本对应关系表选择兼容的 SpringDoc 版本
