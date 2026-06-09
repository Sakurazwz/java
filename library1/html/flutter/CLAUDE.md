# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# 安装依赖
flutter pub get

# 运行应用（Windows 桌面）
flutter run -d windows

# 运行应用（Web）
flutter run -d chrome

# 运行应用（Android）
flutter run -d android

# 构建发布版
flutter build windows
flutter build apk
flutter build web

# 运行测试
flutter test

# 运行单个测试文件
flutter test test/widget_test.dart

# 静态分析
flutter analyze

# 代码格式化
flutter format lib/

# 生成 JSON 序列化代码（如果使用 json_serializable）
flutter pub run build_runner build
```

## Architecture

Flutter 图书管理系统客户端，连接 Spring Boot 后端 API。

### 技术栈
- **状态管理**: flutter_bloc (BLoC 模式)
- **路由**: go_router
- **网络**: Dio (单例模式，自动注入 JWT Token)
- **本地存储**: SharedPreferences
- **JSON 序列化**: 手动 fromJson/toJson（未使用 code generation）

### 目录结构 (`lib/`)

```
lib/
├── main.dart              # 入口，初始化 TokenStorage
├── app.dart               # MaterialApp + GoRouter 路由配置
├── config/
│   └── api_config.dart    # API 地址配置（默认 http://8.163.28.84:1100/api）
├── core/
│   ├── network/
│   │   ├── dio_client.dart   # Dio 单例，拦截器自动注入 Token
│   │   └── api_service.dart  # 所有 API 端点定义
│   ├── storage/
│   │   └── token_storage.dart # SharedPreferences 封装，Token/角色存取
│   └── utils/
│       └── jwt_utils.dart
├── shared/
│   ├── theme/             # 主题配置 (Material 3)
│   ├── constants/         # 应用常量
│   └── widgets/           # 共享组件 (AppDrawer 等)
└── features/              # 按功能模块组织
    ├── auth/              # 认证 (登录/注册)
    ├── books/             # 图书管理
    ├── borrow/            # 借阅管理
    ├── history/           # 借阅历史
    ├── users/             # 用户管理（管理员）
    └── profile/           # 个人中心
```

### Feature 模块内部结构（以 books 为例）

```
books/
├── domain/
│   └── book.dart          # 实体类，手动 fromJson/toJson
├── data/
│   ├── book_api.dart      # API 调用封装（调用 ApiService）
│   └── book_repository.dart # Repository 层，API 数据 → 领域实体
└── presentation/
    ├── list/
    │   ├── books_bloc.dart    # BLoC: Event/State/Bloc
    │   └── books_screen.dart  # UI 页面
    ├── detail/
    │   └── book_detail_screen.dart
    └── form/
        └── book_form_screen.dart
```

### 数据流

UI → Event → BLoC → Repository → Api → DioClient → 后端

BLoC 模式: 每个 feature 有独立的 Event/State/Bloc 类，State 使用抽象类 + 具体子类（Initial/Loading/Loaded/Error）。

### 路由配置 (app.dart)

| 路径 | 页面 | 说明 |
|------|------|------|
| `/login` | LoginScreen | 登录 |
| `/register` | RegisterScreen | 注册 |
| `/books` | BooksScreen | 图书列表 |
| `/books/add` | BookFormScreen | 添加图书 |
| `/books/:id` | BookDetailScreen | 图书详情 |
| `/books/:id/edit` | BookFormScreen | 编辑图书 |
| `/borrow` | BorrowScreen | 我的借阅 |
| `/history` | HistoryScreen | 借阅历史 |
| `/profile` | ProfileScreen | 个人中心 |
| `/users` | UsersScreen | 用户管理（管理员） |

路由守卫: 未登录自动重定向到 `/login`，已登录访问登录页重定向到 `/books`。

### 认证机制

- JWT Token 存储在 SharedPreferences
- Dio 拦截器自动在请求头注入 `Authorization: Bearer <token>`
- 401/403 响应自动清除 Token
- 角色判断: `TokenStorage.isAdmin()` 支持 `'ADMIN'` 和 `'ROLE_ADMIN'` 两种格式

### API 端点

所有 API 调用通过 `ApiService` 类统一管理，基础地址在 `ApiConfig` 中配置。

### Gotchas

- **API 地址**: 默认指向 `http://8.163.28.84:1100/api`，本地开发需修改 `lib/config/api_config.dart`
- **还书 API**: 使用 DELETE 方法 (`DELETE /borrow/back`)，非常规但与后端一致
- **封面图片**: 后端存储 Base64 字符串，前端通过 `cached_network_image` 或 Base64 解码显示
- **角色字段**: 后端可能返回 `'ADMIN'` 或 `'ROLE_ADMIN'`，`TokenStorage.isAdmin()` 两种都处理
- **SharedPreferences 初始化**: `main()` 中必须先调用 `TokenStorage.init()`，否则读取会抛异常
