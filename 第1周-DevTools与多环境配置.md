# 第1周：DevTools 热部署与多环境配置

## 课程目标

通过本周课程，学生将学会：
1. 使用 Spring Initializr 创建 Spring Boot 项目
2. 配置 Spring Boot DevTools 实现热部署
3. 配置多环境（dev/test/prod）管理
4. 掌握 Profile 环境切换的多种方式
5. 理解 Spring Boot 配置文件的加载优先级

---

## 课前准备

### 确认开发环境

在开始之前，请确认以下环境已安装：

- **JDK 21**（已安装）
- **IntelliJ IDEA Community 2025.1.3**（已安装）
- **Maven**（IDEA 自带，无需单独安装）
- **浏览器**（Chrome/Edge，用于访问 start.spring.io）

### 检查 JDK 配置

1. 打开 IntelliJ IDEA
2. 点击 `File` → `Settings`（Windows）或 `IntelliJ IDEA` → `Preferences`（Mac）
3. 搜索 `Build, Execution, Deployment` → `Build Tools` → `Maven`
4. 查看 `Maven home path`，确认使用 IDEA 自带的 Maven
5. 搜索 `Build, Execution, Deployment` → `Compiler` → `Java Compiler`
6. 确认 `Project bytecode version` 为 `21`

---

## 第一部分：创建 Spring Boot 项目（20分钟）

### 步骤1：访问 Spring Initializr

1. 打开浏览器，访问 [https://start.spring.io/](https://start.spring.io/)
2. 页面显示项目初始化配置界面

### 步骤2：配置项目基本信息

在 Spring Initializr 页面上，填写以下信息：

| 选项 | 值 |
|------|-----|
| **Project** | Maven |
| **Language** | Java |
| **Spring Boot** | 3.2.x（选择最新的稳定版本，如 3.2.10） |
| **Group** | `com.example` |
| **Artifact** | `demo` |
| **Name** | `demo` |
| **Package name** | `com.example.demo` |
| **Packaging** | Jar |
| **Java** | 21 |

### 步骤3：添加依赖

点击右侧的 `ADD DEPENDENCIES` 按钮，搜索并添加以下依赖：

1. **Spring Web** - 构建 Web 应用
2. **Spring Boot DevTools** - 热部署工具（本周重点）
3. **Lombok** - 简化代码（可选，但推荐）
4. **Spring Configuration Processor** - 配置文件提示（推荐）

添加完成后，点击底部的 `GENERATE` 按钮下载项目压缩包。

### 步骤4：导入项目到 IDEA

1. 解压下载的 `demo.zip` 文件到某个目录（如 `D:\projects\demo`）
2. 打开 IntelliJ IDEA
3. 点击 `File` → `Open`
4. 选择解压后的项目目录（包含 `pom.xml` 的目录）
5. 点击 `OK`
6. 等待 Maven 下载依赖（右下角会显示进度）
7. 等待索引完成后，项目结构如图所示：

```
demo
├── .mvn
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── demo
│   │   │               └── DemoApplication.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
└── pom.xml
```

---

## 第二部分：理解项目结构（5分钟）

### 核心文件说明

1. **`pom.xml`** - Maven 项目配置文件，定义依赖和构建配置
2. **`DemoApplication.java`** - 程序入口类，包含 `main` 方法
3. **`application.properties`** - Spring Boot 配置文件

### 查看 `pom.xml` 的依赖部分

打开 `pom.xml`，确认以下依赖已添加：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <annotationProcessorPaths>
                <path>
                    <groupId>org.projectlombok</groupId>
                    <artifactId>lombok</artifactId>
                </path>
                <path>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-configuration-processor</artifactId>
                </path>
            </annotationProcessorPaths>
        </configuration>
    </plugin>

```

### 查看 `DemoApplication.java`

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

**说明**：
- `@SpringBootApplication` 是一个组合注解，包含：
  - `@Configuration` - 配置类
  - `@EnableAutoConfiguration` - 自动配置
  - `@ComponentScan` - 组件扫描

---

## 第三部分：测试项目启动（5分钟）

### 步骤1：启动项目

1. 打开 `DemoApplication.java`
2. 点击类名左侧的绿色运行按钮 ▶️，选择 `Run 'DemoApplication'`
3. 观察控制台输出

### 预期输出

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.10)

2025-01-31T10:00:00.000+08:00  INFO 12345 --- [  restartedActive] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2025-01-31T10:00:00.000+08:00  INFO 12345 --- [  restartedActive] com.example.demo.DemoApplication         : Started DemoApplication in 2.5 seconds (JVM running for 3.2)
```

**关键信息**：
- Tomcat 启动在端口 `8080`
- 应用启动成功

### 步骤2：验证应用运行

1. 打开浏览器，访问 `http://localhost:8080`
2. 应该看到错误页面（这是正常的，因为我们还没定义任何接口）

---

## 第四部分：创建第一个 REST 接口（10分钟）

### 步骤1：创建 Controller 类

1. 在 `com.example.demo` 包上右键
2. 选择 `New` → `Java Class`
3. 输入名称 `HelloController`
4. 点击 `OK`

### 步骤2：编写代码

将 `HelloController.java` 的内容替换为以下代码：

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "欢迎使用 Spring Boot！当前时间：" + getCurrentTime();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Hello, " + name + "! 当前时间：" + getCurrentTime();
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
```

**代码说明**：
- `@RestController` - 声明这是一个 REST 控制器
- `@GetMapping` - 定义 GET 请求映射
- `@PathVariable` - 获取路径参数

### 步骤3：测试接口

1. 确保应用正在运行
2. 在浏览器访问以下地址，观察输出：

| URL | 预期输出 |
|-----|----------|
| `http://localhost:8080/` | 欢迎使用 Spring Boot！当前时间：2025-01-31 10:00:00 |
| `http://localhost:8080/hello` | Hello, Spring Boot! |
| `http://localhost:8080/hello/张三` | Hello, 张三! 当前时间：2025-01-31 10:00:00 |

---

## 第五部分：配置 DevTools 热部署（20分钟）

### 什么是热部署？

**热部署（Hot Swap）** 指在应用运行时，修改代码后无需手动重启应用，即可看到修改后的效果。

**DevTools 的作用**：
- 自动监听 classpath 下的文件变化
- 快速重启应用上下文（保留一些元数据，比完全重启快）
- 提高开发效率

### 步骤1：确认 DevTools 依赖

打开 `pom.xml`，确认已有 DevTools 依赖（之前已添加）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### 步骤2：配置 IDEA 自动编译

**重要**：DevTools 需要配合 IDEA 的自动编译功能使用。

#### 配置自动编译

1. 点击 `File` → `Settings`（或 `Ctrl + Alt + S`）
2. 搜索 `Compiler`
3. 勾选 `Build project automatically`（自动构建项目）
4. `Compile independent modules in parallel`（并行编译独立模块）选择'自动的'
5. 点击 `Apply` 和 `OK`

#### 配置运行时编译

1. 按 `Ctrl + Shift + Alt + /`（Windows）或 `Cmd + Shift + Alt + /`（Mac）
2. 选择 `Registry...`
3. 找到并勾选 `compiler.automake.allow.parallel`
4. 点击 `Close`

### 步骤3：测试热部署

#### 测试1：修改 Controller

1. 确保应用正在运行
2. 打开 `HelloController.java`
3. 修改 `home()` 方法的返回值：

```java
@GetMapping("/")
public String home() {
    return "欢迎来到 Spring Boot DevTools 测试！当前时间：" + getCurrentTime();
}
```

4. 按 `Ctrl + S` 保存文件
5. 观察控制台输出，应该看到类似以下内容：

```
[   File Watcher] rtingClassPathChangeChangedEventListener : Restarting due to 
1 class path change (1 addition, 0 deletions, 0 modifications)
```

6. 刷新浏览器 `http://localhost:8080/`
7. 应该看到修改后的内容

**注意**：
- DevTools 使用了两个类加载器：
  - **base classloader** - 加载不变的类（第三方依赖）
  - **restart classloader** - 加载开发中的类（应用代码）
- 只有 restart classloader 会被重新加载，所以重启速度很快

#### 测试2：新增接口

1. 在 `HelloController` 中添加新方法：

```java
@GetMapping("/devtools")
public String devtools() {
    return "DevTools 热部署测试成功！";
}
```

2. 保存文件
3. 等待几秒，让 DevTools 完成重启
4. 访问 `http://localhost:8080/devtools`
5. 应该看到 "DevTools 热部署测试成功！"

### 步骤4：配置 DevTools 属性（可选）

创建 `application.properties` 配置文件，添加 DevTools 相关配置：

```properties
# DevTools 配置

# 启用热部署（默认启用）
spring.devtools.restart.enabled=true

# 排除不触发重启的资源
spring.devtools.restart.exclude=static/**,public/**,templates/**,META-INF/maven/**,META-INF/resources/**

# LiveReload（浏览器自动刷新, 需要安装插件)
spring.devtools.livereload.enabled=true

# 日志级别
logging.level.org.springframework.boot.devtools=INFO
```

**说明**：
- `restart.exclude` - 指定哪些资源变化不触发重启（如静态文件）

---

## 第六部分：多环境配置（30分钟）

### 为什么需要多环境配置？

在实际开发中，应用需要在不同环境运行：
- **开发环境（dev）** - 开发人员本地开发
- **测试环境（test）** - 测试人员测试
- **生产环境（prod）** - 正式上线

每个环境的配置可能不同：
- 数据库连接
- Redis 地址
- 日志级别
- 端口号
- 外部服务地址

### Spring Boot 多环境配置方案

Spring Boot 支持多种多环境配置方式：

1. **多 Profile 文件**（推荐）- `application-{profile}.yml`
2. **多 Profile 文件**（properties）- `application-{profile}.properties`
3. **单一文件多 Profile** - 在一个文件中用 `---` 分隔
4. **环境变量** - 操作系统环境变量
5. **命令行参数** - 启动时指定

**本课程重点讲解方案1和方案2。**

---

## 第七部分：配置文件格式选择（5分钟）

### YAML vs Properties

Spring Boot 支持两种配置文件格式：

| 格式 | 扩展名 | 优点 | 缺点 |
|------|--------|------|------|
| **Properties** | `.properties` | 简单直观、层次不敏感 | 不支持复杂结构、不直观 |
| **YAML** | `.yml` 或 `.yaml` | 支持复杂结构、可读性好 | 缩进敏感 |

**推荐**：使用 YAML 格式（`.yml`）

### 步骤1：删除默认配置文件

1. 打开 `src/main/resources/application.properties`
2. 删除该文件（或重命名为 `application.properties.bak` 备份）

### 步骤2：创建 YAML 配置文件

1. 右键 `src/main/resources` 目录
2. 选择 `New` → `File`
3. 输入名称 `application.yml`
4. 点击 `OK`

---

## 第八部分：创建多环境配置文件（15分钟）

### 配置文件命名规范

```
application.yml               # 主配置文件（默认配置）
application-dev.yml           # 开发环境配置
application-test.yml          # 测试环境配置
application-prod.yml          # 生产环境配置
```

### 步骤1：配置主文件（application.yml）

创建 `src/main/resources/application.yml`：

```yaml
# 主配置文件
spring:
  application:
    name: demo

  # 激活的 Profile（默认为 dev）
  profiles:
    active: dev

# 应用端口
server:
  port: 8080

# 应用信息
app:
  name: Spring Boot 多环境配置示例
  version: 1.0.0
  author: 你的名字
```

**说明**：
- `spring.profiles.active` 指定默认激活的环境为 `dev`
- 可以通过多种方式覆盖这个值（后面会讲）

### 步骤2：配置开发环境（application-dev.yml）

创建 `src/main/resources/application-dev.yml`：

```yaml
# 开发环境配置
server:
  port: 8080

spring:
  # 数据源配置（暂时使用 H2 内存数据库）
  datasource:
    url: jdbc:h2:mem:devdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  # H2 数据库控制台
  h2:
    console:
      enabled: true
      path: /h2-console

  # Redis 配置（如果本地安装了 Redis，可以启用）
  redis:
    host: localhost
    port: 6379
    database: 0

# 日志配置
logging:
  level:
    root: INFO
    com.example.demo: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

# 应用配置
app:
  environment: 开发环境
  debug: true

# 自定义配置
custom:
  upload-path: D:/upload/dev
  max-file-size: 10MB
```

**注意**：Redis 配置默认不会生效，因为项目没有添加 Redis 依赖。如果需要测试，需要在 `pom.xml` 添加 Redis 依赖。

### 步骤3：配置测试环境（application-test.yml）

创建 `src/main/resources/application-test.yml`：

```yaml
# 测试环境配置
server:
  port: 8081

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

logging:
  level:
    root: INFO
    com.example.demo: INFO

app:
  environment: 测试环境
  debug: true

custom:
  upload-path: /data/upload/test
  max-file-size: 5MB
```

### 步骤4：配置生产环境（application-prod.yml）

创建 `src/main/resources/application-prod.yml`：

```yaml
# 生产环境配置
server:
  port: 8082

spring:
  datasource:
    url: jdbc:h2:mem:proddb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: false  # 生产环境关闭 H2 控制台

logging:
  level:
    root: WARN
    com.example.demo: INFO
  file:
    name: logs/application.log

app:
  environment: 生产环境
  debug: false

custom:
  upload-path: /data/upload/prod
  max-file-size: 2MB
```

---

## 第九部分：读取配置值（10分钟）

### 步骤1：创建配置类

创建 `com.example.demo.config.AppConfig.java`：

```java
package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 环境
     */
    private String environment;

    /**
     * 是否调试模式
     */
    private Boolean debug;
}
```

**说明**：
- `@ConfigurationProperties(prefix = "app")` - 自动绑定 `app` 前缀的配置
- `@Component` - 注册为 Spring Bean
- `@Data` - Lombok 注解，自动生成 getter/setter

### 步骤2：创建另一个配置类

创建 `com.example.demo.config.CustomConfig.java`：

```java
package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "custom")
public class CustomConfig {

    /**
     * 文件上传路径
     */
    private String uploadPath;

    /**
     * 最大文件大小
     */
    private String maxFileSize;
}
```

### 步骤3：在 Controller 中使用配置

修改 `HelloController.java`，添加新的接口：

```java
package com.example.demo;

import com.example.demo.config.AppConfig;
import com.example.demo.config.CustomConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private Environment environment;

    @GetMapping("/")
    public String home() {
        return "欢迎来到 Spring Boot DevTools 测试！当前时间：" + getCurrentTime();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Hello, " + name + "! 当前时间：" + getCurrentTime();
    }

    /**
     * 获取应用配置信息
     */
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("appName", appConfig.getName());
        config.put("version", appConfig.getVersion());
        config.put("author", appConfig.getAuthor());
        config.put("environment", appConfig.getEnvironment());
        config.put("debug", appConfig.getDebug());
        config.put("uploadPath", customConfig.getUploadPath());
        config.put("maxFileSize", customConfig.getMaxFileSize());
        config.put("serverPort", environment.getProperty("server.port"));
        config.put("activeProfile", environment.getProperty("spring.profiles.active"));
        return config;
    }

    /**
     * 获取当前环境信息
     */
    @GetMapping("/env")
    public String getEnv() {
        return String.format("当前环境：%s，端口：%s，调试模式：%s",
                appConfig.getEnvironment(),
                environment.getProperty("server.port"),
                appConfig.getDebug());
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
```

**说明**：
- `@Autowired` - 自动注入配置类
- `Environment` - Spring 的环境抽象，可以获取任何配置值
- 提供了三个接口查看配置信息

### 步骤4：测试配置读取

1. 启动应用
2. 访问 `http://localhost:8080/config`

**预期输出（dev 环境）**：

```json
{
    "appName": "Spring Boot 多环境配置示例",
    "version": "1.0.0",
    "author": "你的名字",
    "environment": "开发环境",
    "debug": true,
    "uploadPath": "D:/upload/dev",
    "maxFileSize": "10MB",
    "serverPort": "8080",
    "activeProfile": "dev"
}
```

3. 访问 `http://localhost:8080/env`

**预期输出**：

```
当前环境：开发环境，端口：8080，调试模式：true
```

---

## 第十部分：切换 Profile 的多种方式（15分钟）

### 方式1：修改配置文件（最简单）

修改 `application.yml` 中的 `spring.profiles.active`：

```yaml
spring:
  profiles:
    active: test  # 改为 test
```

重启应用，访问 `http://localhost:8081/config`（注意端口变为 8081）

**预期输出**：

```json
{
    "appName": "Spring Boot 多环境配置示例",
    "environment": "测试环境",
    "serverPort": "8081",
    "activeProfile": "test"
}
```

---

### 方式2：IDEA 运行配置参数（推荐开发时使用, 社区版IDEA不支持)

1. 点击 IDEA 右上角的运行配置下拉框
2. 选择 `Edit Configurations...`
3. 选择 `DemoApplication`
4. 在 `Active profiles` 输入框中填写 `prod`
5. 点击 `OK`
6. 重新运行应用

或者：

1. 在 `Active profiles` 中填写多个 profile，用逗号分隔：`dev,redis`
2. 点击 `OK`
3. 运行应用

---

### 方式3：命令行参数（推荐生产环境使用）

打包项目后，使用命令行参数启动：

```bash
# Windows
java -jar demo.jar --spring.profiles.active=prod

# Linux/Mac
java -jar demo.jar --spring.profiles.active=prod
```

---

### 方式4：环境变量（推荐容器化部署）

设置操作系统环境变量：

```bash
# Windows (cmd)
set SPRING_PROFILES_ACTIVE=prod
java -jar demo.jar

# Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE="prod"
java -jar demo.jar

# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
java -jar demo.jar
```

---

### 方式5：Maven Profile（高级用法）

在 `pom.xml` 中配置 Maven Profile：

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <profile.active>dev</profile.active>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    <profile>
        <id>test</id>
        <properties>
            <profile.active>test</profile.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <profile.active>prod</profile.active>
        </properties>
    </profile>
</profiles>
```

在 `application.yml` 中引用：

```yaml
spring:
  profiles:
    active: @profile.active@
```

打包时指定 Profile：

```bash
mvn clean package -Pprod
```

---

## 第十一部分：配置文件优先级（5分钟）

### Spring Boot 配置加载优先级（从高到低）

1. **命令行参数** - `--spring.profiles.active=prod`
2. **系统环境变量** - `SPRING_PROFILES_ACTIVE`
3. **外部配置文件** - `./config/application-{profile}.yml`
4. **外部配置文件** - `./application-{profile}.yml`
5. **内部配置文件** - `classpath:application-{profile}.yml`
6. **内部配置文件** - `classpath:application.yml`
7. **默认配置** - `SpringApplication` 默认属性

### 实战演示(社区版不支持）

**目标**：验证命令行参数优先级最高

1. 在 `application.yml` 中设置 `spring.profiles.active: dev`
2. 在 IDEA 运行配置中设置 `Active profiles: prod`
3. 运行应用
4. 访问 `http://localhost:8082/config`（注意端口是 8082）

**结论**：IDEA 运行配置（命令行参数）覆盖了配置文件中的值。

---

## 第十二部分：使用 Profile 进行条件化配置（10分钟）

### 场景描述

某些 Bean 只在特定环境下生效。例如，我们希望：
- 开发环境使用 H2 内存数据库
- 生产环境使用 MySQL 真实数据库

### 推荐方式：使用 @Profile 注解

这是 Spring Boot 的标准做法，直接在 Service 类上使用 `@Profile` 注解。

### 创建服务接口

创建 `com.example.demo.service.DataService.java`：

```java
package com.example.demo.service;

/**
 * 数据服务接口
 */
public interface DataService {
    String getData();
}
```

### 创建开发环境的 Service 实现

创建 `com.example.demo.service.DevDataService.java`：

```java
package com.example.demo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 开发环境的数据服务
 */
@Service  // ✅ 加上 @Service 注解
@Profile("dev")  // ✅ 只在 dev 环境生效
public class DevDataService implements DataService {

    @Override
    public String getData() {
        return "开发环境数据（使用 H2 内存数据库）";
    }
}
```

**说明**：
- `@Service` - 将类注册为 Spring Bean
- `@Profile("dev")` - 只在 dev 环境时创建此 Bean

---

### 创建生产环境的 Service 实现

创建 `com.example.demo.service.ProdDataService.java`：

```java
package com.example.demo.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 生产环境的数据服务
 */
@Service  // ✅ 加上 @Service 注解
@Profile("prod")  // ✅ 只在 prod 环境生效
public class ProdDataService implements DataService {

    @Override
    public String getData() {
        return "生产环境数据（使用 MySQL 真实数据库）";
    }
}
```

---

### 工作原理

```
application.yml 中配置：
spring:
  profiles:
    active: dev

Spring 容器启动时：
1. 扫描所有 @Service、@Component 等注解
2. 检查每个 Bean 上的 @Profile 注解
3. 只创建匹配当前环境的 Bean
4. DevDataService 会被创建（因为 @Profile("dev")）
5. ProdDataService 不会被创建（因为 @Profile("prod")）
```

---

### 在 Controller 中使用

在 `HelloController` 中添加测试接口：

```java
@Autowired(required = false)
private DataService dataService;

/**
 * 获取环境相关数据
 */
@GetMapping("/data")
public String getData() {
    if (dataService == null) {
        return "当前环境未配置数据服务";
    }
    return dataService.getData();
}
```

**说明**：
- `required = false` - 允许注入失败（某些环境可能没有对应的 DataService）
- 如果 `dataService` 为 null，返回提示信息

---

### 测试步骤

#### 测试1：dev 环境

1. 确认 `application.yml` 中配置为 dev

   ```yaml
   spring:
     profiles:
       active: dev
   ```

2. 运行应用

3. 访问 `http://localhost:8080/data`

**预期输出**：

```
开发环境数据（使用 H2 内存数据库）
```

---

#### 测试2：prod 环境

1. 修改 `application.yml`

   ```yaml
   spring:
     profiles:
       active: prod
   ```

2. 重新运行应用

3. 访问 `http://localhost:8082/data`

**预期输出**：

```
生产环境数据（使用 MySQL 真实数据库）
```

---

#### 测试3：test 环境（未配置 Service）

1. 修改 `application.yml`

   ```yaml
   spring:
     profiles:
       active: test
   ```

2. 重新运行应用

3. 访问 `http://localhost:8081/data`

**预期输出**：

```
当前环境未配置数据服务
```

---

### @Profile 注解的更多用法

#### 用法1：单个环境

```java
@Service
@Profile("dev")
public class DevDataService {
    // 只在 dev 环境生效
}
```

#### 用法2：多个环境

```java
@Service
@Profile({"dev", "test"})  // dev 或 test 环境都生效
public class DevDataService {
    // 在 dev 和 test 环境都会创建
}
```

#### 用法3：否定表达式

```java
@Service
@Profile("!prod")  // 非 prod 环境都生效
public class DevDataService {
    // 在 dev、test 等环境生效，但不在 prod 环境
}
```

---

### 重要说明

#### ✅ 正确做法

```java
// Service 类加上 @Service 和 @Profile
@Service
@Profile("dev")
public class DevDataService implements DataService {
    // ...
}
```

#### ❌ 错误做法

```java
// 不要同时在 Service 类上加 @Service，又在 @Configuration 中用 @Bean 定义
@Service
@Profile("dev")
public class DevDataService implements DataService {
    // ...
}

// 同时又在配置类中定义
@Configuration
public class Config {
    @Bean
    @Profile("dev")
    public DataService devDataService() {
        return new DevDataService();  // ❌ 会和上面的 @Service 冲突
    }
}
```

**原因**：会导致 Bean 定义冲突错误。

---

### 何时使用 @Bean + @Profile？

只有当 Bean 创建过程比较复杂，需要额外配置时，才使用 `@Bean` 方式。

**示例**：创建 DataSource 时需要复杂配置

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("prod")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        dataSource.setUsername("root");
        dataSource.setPassword("password");
        // ... 更多配置

        return dataSource;
    }
}
```

对于简单的 Service 类，**推荐使用 `@Service` + `@Profile`**。

---

## 第十三部分：打包和部署（10分钟）

### 步骤1：添加打包插件

确保 `pom.xml` 中有以下插件（Spring Boot Initializr 默认会添加）：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 步骤2：打包项目

**方式1：使用 IDEA Maven 插件**

1. 打开右侧的 `Maven` 面板
2. 展开 `Lifecycle`
3. 双击 `clean`，等待完成
4. 双击 `package`，等待完成

**方式2：使用 Maven 命令**

打开 IDEA 底部的 Terminal，执行：

```bash
mvn clean package
```

**方式3：打包并跳过测试**

```bash
mvn clean package -DskipTests
```

### 步骤3：查看打包结果

打包成功后，在 `target` 目录下会生成：
- `demo-0.0.1-SNAPSHOT.jar` - 可执行 JAR（包含依赖）
- `demo-0.0.1-SNAPSHOT.jar.original` - 原始 JAR（不包含依赖）

### 步骤4：运行打包好的 JAR

打开 Terminal，进入 `target` 目录：

```bash
# 使用 dev 环境
java -jar demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 使用 prod 环境
java -jar demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 步骤5：验证

访问对应的端口和 `/config` 接口，验证环境是否正确切换。

---

## 课后作业

### 必做题（基础）

1. **创建新项目**
   - 使用 Spring Initializr 创建新项目
   - 项目名称：`profile-demo`
   - 添加 DevTools、Lombok、Web 依赖

2. **配置多环境**
   - 创建 dev、test、prod 三个环境配置文件
   - 每个环境配置不同的端口（8080、8081、8082）
   - 每个环境配置不同的应用名称

3. **创建接口**
   - 创建 `InfoController`
   - 实现接口 `/info`，返回当前环境信息（环境名、端口、应用名）
   - 实现接口 `/switch/{profile}`，动态切换环境（提示用户重启）

4. **测试热部署**
   - 启动应用后，修改 `InfoController` 的返回值
   - 验证 DevTools 是否自动生效
   - 记录重启时间（观察控制台输出）

### 选做题（进阶）

1. **配置类实践**
   - 创建 `DatabaseConfig` 配置类
   - 读取 `datasource.url`、`datasource.username`、`datasource.password`
   - 创建接口 `/datasource`，返回数据库配置信息

2. **条件化 Bean**
   - 创建 `EmailService` 接口
   - 实现 `DevEmailService`（打印日志）和 `ProdEmailService`（模拟发送邮件）
   - 使用 `@Profile` 注解实现环境切换

3. **打包测试**
   - 将项目打包为 JAR
   - 分别使用 dev、test、prod 环境启动
   - 验证每个环境的配置是否正确

4. **外部配置文件**
   - 在项目外创建 `config/application-prod.yml`
   - 覆盖内部配置的端口（改为 9090）
   - 验证外部配置的优先级

### 挑战题（额外）

1. **实现配置刷新**
   - 研究如何在不重启应用的情况下刷新配置
   - 提示：可以使用 `@RefreshScope`（需要 Spring Cloud）

2. **多 Profile 组合**
   - 创建 `application-dev.yml`、`application-redis.yml`、`application-mq.yml`
   - 启动时同时激活多个 profile：`dev,redis`
   - 配置文件的加载顺序是什么？

---

## 常见问题解答

### Q1：DevTools 不生效怎么办？

**可能原因**：
1. IDEA 未开启自动编译
2. Registry 中未设置 `compiler.automake.allow.parallel`
3. 依赖 scope 不是 runtime

**解决方案**：
1. 检查 IDEA 设置中的 `Build project automatically`
2. 检查 Registry 配置
3. 检查 `pom.xml` 中的 DevTools 依赖

---

### Q2：配置文件不生效？

**检查清单**：
1. 文件名是否正确（`application-{profile}.yml`）
2. 文件是否在 `src/main/resources` 目录下
3. YAML 缩进是否正确（使用空格，不要用 Tab）
4. 是否激活了对应的 Profile

---

### Q3：如何查看当前激活的 Profile？

**方式1**：访问 `/config` 接口（如果已实现）

**方式2**：查看启动日志

```
The following 1 profile is active: "dev"
```

**方式3**：使用 Environment

```java
@Autowired
private Environment environment;

public String[] getActiveProfiles() {
    return environment.getActiveProfiles();
}
```

---

### Q4：YAML 和 Properties 可以混用吗？

**答案**：可以，但不推荐。

如果同时存在 `application.yml` 和 `application.properties`：
- `application.properties` 的优先级更高
- 建议统一使用一种格式

---

### Q5：如何加密敏感配置（如数据库密码）？

**方案**：
1. 使用 Jasypt 加密
2. 添加依赖：

```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

3. 配置加密密码：

```yaml
jasypt:
  encryptor:
    password: my-secret-key
```

4. 加密配置：

```yaml
datasource:
  password: ENC(加密后的密码)
```

---

## 扩展阅读

1. **Spring Boot 官方文档**
   - [Configuration Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
   - [Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)

2. **YAML 语法**
   - [YAML 官方网站](https://yaml.org/)
   - [YAML 在线验证工具](https://www.yamllint.com/)

3. **DevTools 文档**
   - [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)

---

## 下周预告

**第2周：统一响应 + 异常处理 + 参数校验**

- 统一返回结构 `Result<T>`
- 全局异常处理 `@ControllerAdvice`
- 参数校验 `@Valid`
- 自定义业务异常

---

## 教学反思点

课后请思考：

1. DevTools 的热部署机制是否理解？
2. 多环境配置的优先级是否掌握？
3. YAML 和 Properties 格式哪个更适合？
4. 如何在实际项目中组织配置文件？
5. 是否有更好的多环境管理方案？

---

**文档版本**：v1.0
**最后更新**：2025-01-31
**适用版本**：Spring Boot 3.2.x、JDK 21
