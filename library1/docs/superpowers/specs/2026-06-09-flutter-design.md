# Flutter 跨平台客户端设计方案

## 概述

为图书管理系统构建 Flutter 跨平台客户端，支持 Windows 桌面和 Android 移动端，复用现有后端 Spring Boot REST API。

## 项目结构

```
html/flutter/
├── lib/
│   ├── main.dart                    # 应用入口
│   ├── app.dart                     # MaterialApp 配置和路由
│   ├── config/
│   │   └── api_config.dart          # API 地址配置
│   ├── core/
│   │   ├── network/
│   │   │   ├── dio_client.dart      # Dio 实例和拦截器
│   │   │   └── api_service.dart     # API 接口定义
│   │   ├── storage/
│   │   │   └── token_storage.dart   # Token 本地存储
│   │   └── utils/
│   │       └── jwt_utils.dart       # JWT 解析工具
│   ├── features/
│   │   ├── auth/
│   │   │   ├── data/
│   │   │   │   ├── auth_repository.dart
│   │   │   │   └── auth_api.dart
│   │   │   ├── domain/
│   │   │   │   └── user.dart
│   │   │   └── presentation/
│   │   │       ├── login/
│   │   │       │   ├── login_screen.dart
│   │   │       │   └── login_bloc.dart
│   │   │       └── register/
│   │   │           ├── register_screen.dart
│   │   │           └── register_bloc.dart
│   │   ├── books/
│   │   │   ├── data/
│   │   │   │   ├── book_repository.dart
│   │   │   │   └── book_api.dart
│   │   │   ├── domain/
│   │   │   │   └── book.dart
│   │   │   └── presentation/
│   │   │       ├── list/
│   │   │       │   ├── books_screen.dart
│   │   │       │   └── books_bloc.dart
│   │   │       ├── detail/
│   │   │       │   └── book_detail_screen.dart
│   │   │       └── form/
│   │   │           └── book_form_screen.dart
│   │   ├── borrow/
│   │   │   ├── data/
│   │   │   │   ├── borrow_repository.dart
│   │   │   │   └── borrow_api.dart
│   │   │   ├── domain/
│   │   │   │   └── borrow_record.dart
│   │   │   └── presentation/
│   │   │       ├── borrow_screen.dart
│   │   │       └── borrow_bloc.dart
│   │   ├── history/
│   │   │   ├── data/
│   │   │   │   ├── history_repository.dart
│   │   │   │   └── history_api.dart
│   │   │   ├── domain/
│   │   │   │   └── borrow_history.dart
│   │   │   └── presentation/
│   │   │       ├── history_screen.dart
│   │   │       └── history_bloc.dart
│   │   ├── profile/
│   │   │   ├── presentation/
│   │   │   │   ├── profile_screen.dart
│   │   │   │   └── profile_bloc.dart
│   │   │   └── widgets/
│   │   │       └── profile_header.dart
│   │   └── users/
│   │       ├── data/
│   │       │   ├── user_repository.dart
│   │       │   └── user_api.dart
│   │       └── presentation/
│   │           ├── users_screen.dart
│   │           └── users_bloc.dart
│   └── shared/
│       ├── theme/
│       │   ├── app_theme.dart
│       │   └── app_colors.dart
│       ├── widgets/
│       │   ├── app_drawer.dart
│       │   ├── loading_indicator.dart
│       │   └── error_widget.dart
│       └── constants/
│           └── app_constants.dart
├── android/                         # Android 平台文件
├── windows/                         # Windows 平台文件
├── test/                            # 测试文件
└── pubspec.yaml                     # 依赖配置
```

## 架构设计

### 分层架构

采用 Clean Architecture 分层：

1. **Presentation 层** - UI 组件 + Bloc 状态管理
2. **Domain 层** - 业务实体和 Repository 接口
3. **Data 层** - Repository 实现 + API 调用

### 网络层 (`core/network/`)

- **DioClient** - 单例 Dio 实例，配置拦截器：
  - 请求拦截器：自动注入 JWT Token
  - 响应拦截器：处理 401/403 自动跳转登录
  - 错误拦截器：统一错误处理
- **ApiService** - 定义所有 API 接口

### 状态管理 (`features/*/presentation/*_bloc.dart`)

使用 Bloc 模式：
- **Event** - 用户操作事件
- **State** - UI 状态
- **Bloc** - 业务逻辑处理

### 路由 (`app.dart`)

使用 go_router：
- `/login` - 登录页
- `/register` - 注册页
- `/` 和 `/books` - 图书列表
- `/books/:id` - 图书详情
- `/books/add` - 添加图书（管理员）
- `/books/:id/edit` - 编辑图书（管理员）
- `/borrow` - 借阅管理
- `/history` - 借阅历史
- `/profile` - 个人中心
- `/users` - 用户管理（管理员）

### 平台适配

**Windows 桌面：**
- 窗口尺寸：1200x800
- 支持窗口缩放
- 侧边栏导航

**Android 移动端：**
- 响应式布局
- 底部导航栏
- 支持下拉刷新

## 依赖配置 (pubspec.yaml)

```yaml
name: library1_flutter
description: 图书管理系统 Flutter 客户端
publish_to: 'none'
version: 1.0.0

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter
  
  # 状态管理
  flutter_bloc: ^8.1.3
  equatable: ^2.0.5
  
  # 网络请求
  dio: ^5.4.0
  
  # 路由
  go_router: ^13.0.0
  
  # 本地存储
  shared_preferences: ^2.2.2
  
  # UI 组件
  flutter_svg: ^2.0.9
  cached_network_image: ^3.3.1
  
  # 工具
  intl: ^0.19.0
  json_annotation: ^4.8.1

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.1
  build_runner: ^2.4.8
  json_serializable: ^6.7.1

flutter:
  uses-material-design: true
  assets:
    - assets/images/
```

## API 接口复用

与现有后端完全兼容：

| 模块 | 接口 | 方法 |
|------|------|------|
| 认证 | /api/auth/login | POST |
| 用户 | /api/users/register | POST |
| 用户 | /api/users/all | GET |
| 用户 | /api/users/delete/{id} | DELETE |
| 用户 | /api/users/updateRole/{id} | PUT |
| 图书 | /api/books/getAllBooks | GET |
| 图书 | /api/books/getBookById/{id} | GET |
| 图书 | /api/books/getBookByTitle | GET |
| 图书 | /api/books/addBook | POST |
| 图书 | /api/books/updateBook | PUT |
| 图书 | /api/books/deleteBook/{id} | DELETE |
| 图书 | /api/books/recommend | GET |
| 借阅 | /api/borrow/all | GET |
| 借阅 | /api/borrow/add | POST |
| 借阅 | /api/borrow/back | DELETE |
| 借阅 | /api/borrow/updateBorrow | POST |
| 借阅 | /api/borrow/user | GET |
| 历史 | /api/borrowhistory/all | GET |
| 历史 | /api/borrowhistory/getBorrowHistoryByUserId/{id} | GET |

## 验证标准

1. `flutter run -d windows` 启动 Windows 桌面版正常
2. `flutter run -d android` 启动 Android 版正常
3. 登录、图书列表、借阅、历史、个人中心等所有页面正常工作
4. 管理员功能（用户管理）正常
5. 响应式布局在不同屏幕尺寸下正常
6. `flutter build windows` 生成 Windows 可执行文件
7. `flutter build apk` 生成 Android APK
