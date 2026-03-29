<h1 style="text-align: center;">广 州 商 学 院</h1>
<h1 style="text-align: center;">实验报告（第 1 次）</h2>
<h3 style="text-align: center;">实验名称 ________________      实验时间 _______________</h3>
<h3 style="text-align: center;">同组同学 ________________      小组分工 _______________</h3>

## 一、实验目的

1. 理解 Spring Boot DevTools 的作用与使用方式。  
2. 掌握 Spring Boot 多环境配置（dev���test、prod）的基本方法。  
3. 学会使用 Maven 将 Spring Boot 项目打包为可运行 JAR。  
4. 能够通过 `--spring.profiles.active` 启动不同环境并验证配置是否生效。  
5. 掌握常见运行问题排查方法（如 JAR 签名异常、JAVA_HOME 配置错误）。

## 二、实验仪器设备或材料

1. 操作系统：Windows  
2. 开发工具：IntelliJ IDEA（或同类 Java IDE）  
3. JDK：21  
4. 构建工具：Maven（使用 `mvnw`）  
5. 实验项目：`Sakurazwz/java` 仓库中的 `profile-demo`  
6. 主要依赖：  
   - `spring-boot-starter-web`  
   - `spring-boot-devtools`  
   - `spring-boot-starter-actuator`  
   - `spring-cloud-starter`

## 三、实验原理

1. **DevTools 热部署原理**  
   DevTools 通过监听类路径下资源变化，触发应用自动重启（开发阶段），提升调试效率。  

2. **Spring Profiles 多环境原理**  
   使用 `application-{profile}.yml`（如 `application-dev.yml`、`application-test.yml`、`application-prod.yml`）将环境配置隔离。  
   启动时通过参数指定：  
   `--spring.profiles.active=dev/test/prod`，框架自动加载对应配置。  

3. **Maven 打包原理**  
   通过 `mvn clean package` 构建项目，`spring-boot-maven-plugin` 会生成可执行 fat jar，  
   使用 `java -jar` 即可运行。  

4. **异常原理（本次遇到）**  
   - `Invalid signature file digest for Manifest main attributes`：通常是运行了错误产物、jar内容不一致或缓存污染导致。  
   - `JAVA_HOME environment variable is not defined correctly`：系统未正确配置 JDK 路径，Maven Wrapper 无法调用 Java。

## 四、实验内容与步骤

1. **检查并整理项目依赖**  
   查看 `pom.xml`，确认包含 DevTools 与 Actuator。发现 actuator 依赖重复，进行精简（保留一份）。  

2. **执行打包命令**  
   初次执行：  
   `mvnw -U clean package -DskipTests`  
   
3. **配置 JDK 环境变量**  
   - 设置 `JAVA_HOME` 指向 JDK 21 安装目录。  
   - 在 `Path` 中添加 `%JAVA_HOME%\bin`。  
   - 验证：`java -version`、`mvnw -v`。  

4. **重新打包**  
   再次执行：  
   `mvnw -U clean package -DskipTests`  
   生成 `target/profile-demo-0.0.1-SNAPSHOT.jar`。  

5. **启动多环境并验证**  
   在项目根目录文件下分别执行并且浏览器访问对应的端口：  
   
   ```cmd
   java -jar target/profile-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
   ```
   
   ```json
   {
     "activeProfiles": [
       "dev"
     ],
     "appName": "profile-demo",
     "port": "8080",
     "envName": "dev",
     "featureFlag": true,
     "welcome": "Hello from DEV"
   }
   ```
   
   ```cmd
   java -jar target/profile-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=test 
   ```
   
   ```json
   {
     "activeProfiles": [
       "test"
     ],
     "appName": "profile-demo",
     "port": "8082",
     "envName": "test",
     "featureFlag": false,
     "welcome": "Hello from TEST"
   }
   ```
   
   ```cmd
   java -jar target/profile-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   ```
   
   ```json
   {
     "activeProfiles": [
       "prod"
     ],
     "appName": "profile-demo",
     "port": "8081",
     "envName": "prod",
     "featureFlag": false,
     "welcome": "Hello from PROD"
   }
   ```
   
7. **问题排查记录**  
   
   - 遇到 JNI/签名报错后，确认应运行重打包后的 jar（非 `.jar.original`）。  
   - 通过 `clean + package` 重新构建，避免旧产物干扰。  

## 五、实验结果与分析

1. **打包结果**  

   执行：  

   ```cmd
   mvnw -U clean package -DskipTests
   ```

   项目可成功打包为可执行 JAR：

   profile-demo-0.0.1-SNAPSHOT.jar。 

   

2. **多环境启动结果**  
   在指定参数后，应用能够按 dev/test/prod 启动，日志显示

   ```cmd
   INFO 11192 --- [profile-demo] [  restartedMain] c.e.profiledemo.ProfileDemoApplication: The following 1 profile is active: "dev"#根据环境变化
   ```

   对应环境的的profile 生效。

3. **配置验证结果**  
   访问/switch/{profile}，显示：

   ```json
   {
     "success": true,
     "activeProfilesNow": [
       "prod"//显示切换的环境
     ],
     "refreshedKeysCount": 0,
     "refreshedKeys": [],
     "message": "已尝试热刷新业务配置（端口不会变化）"
   }
   ```

   再次访问/info，显示：

   ```json
   {
     "activeProfiles": [
       "prod"
     ],
     "appName": "profile-demo",
     "port": "8080",
     "envName": "dev",
     "featureFlag": true,
     "welcome": "Hello from DEV"
   }
   ```

4. **实验结论性分析**  
   本实验证明：规范的 Maven 打包流程 + 明确的 profile 分层 + 正确的 JDK 环境配置，是 Spring Boot 多环境开发与测试的关键。

5. **实现配置刷新（不重启应用）**

   （1）原理说明

   ​	在 Spring Boot + Spring Cloud 场景中，可通过 `@RefreshScope` + Actuator 的刷新端点，在不重启整个应用的情况下，让指定 Bean 重新加载配置。  

   ​	当配置中心或本地环境变量变化后，触发刷新操作，`@RefreshScope` 标注的 Bean 会重新实例化并读取最新配置。

   （2）依赖与配置要求

   1. 需要 Spring Cloud 相关依赖（如项目已引入 `spring-cloud-starter`，可结合实际版本使用）。
   2. 开启 Actuator 刷新端点（Spring Boot 2 常见是 `/actuator/refresh`；Spring Boot 3 + Spring Cloud 新方案常使用 `/actuator/env` + `/actuator/restart` 或 Spring Cloud Bus 方案，具体以当前依赖版本为准）。
   3. 暴露必要管理端点，例如：
      - `management.endpoints.web.exposure.include=health,info,refresh`

6. 加载顺序与覆盖规则

   可按“先基础，后激活；同优先级下后者覆盖前者”理解：

   1. 先加载 `application.yml`（公共配置）。  
   2. 再按 `spring.profiles.active` 指定的 profile 顺序加载：  
      - 先 `application-dev.yml`
      - 后 `application-redis.yml`
   3. 若同名配置项冲突，后加载的值覆盖先加载的值，因此在 `dev,redis` 中，`redis` 对冲突键优先级更高。  

## 六、结论与体会

1. 掌握了 Spring Boot 多环境配置与切换方式，能够独立完成 dev/test/prod 的打包与启动验证。  
2. 熟悉了 DevTools 在开发阶段的辅助作用，理解其与生产运行方式的区别。  
3. 提升了问题定位能力：能够根据报错快速区分“代码问题”与“环境/构建问题”。  
4. 体会到工程实践中“构建规范化、环境标准化”的重要性。后续将完善启动脚本与检查清单，提高实验与开发效率。  

## 七、教师评语

