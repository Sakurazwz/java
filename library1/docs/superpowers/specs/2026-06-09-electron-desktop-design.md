# Electron 桌面端设计方案

## 概述

为图书管理系统添加 Windows 桌面客户端，基于 Electron + React 构建，复用现有前端全部功能，通过 Spring Boot REST API 通信。

## 项目结构

```
html/electron/
├── package.json
├── electron-builder.yml          # 打包配置
├── electron/
│   ├── main.js                   # Electron 主进程
│   └── preload.js                # 预加载脚本（contextBridge）
├── src/
│   ├── main.jsx                  # React 入口
│   ├── App.jsx                   # 路由定义
│   ├── App.css                   # 全局样式
│   ├── components/
│   │   ├── AiRecommend.jsx       # AI 推荐浮窗
│   │   └── AiRecommend.css
│   ├── pages/
│   │   └── user/
│   │       ├── header/Header.jsx + .css
│   │       ├── login/login.jsx + .css
│   │       ├── adduser/adduser.jsx + .css
│   │       ├── books/books.jsx + .css
│   │       ├── borrow/borrow.jsx + .css
│   │       ├── history/history.jsx + .css
│   │       ├── profile/profile.jsx + .css
│   │       ├── users/users.jsx + .css
│   │       └── overview/overview.jsx + .css
│   └── services/
│       └── api.js                # API 封装（可配置基础 URL）
├── vite.config.js
└── index.html
```

## 架构设计

### Electron 主进程 (`electron/main.js`)

- 创建主窗口 1200x800，加载 Vite 开发服务器或构建产物
- 应用菜单：文件（设置 API 地址、退出）、视图（缩放、开发者工具）、帮助（关于）
- 系统托盘：窗口关闭时最小化到托盘，双击托盘恢复窗口
- 单实例锁：防止重复启动

### 预加载脚本 (`electron/preload.js`)

通过 `contextBridge` 暴露安全 API：
- `electron.getAppVersion()` — 获取应用版本
- `electron.getApiUrl()` / `electron.setApiUrl(url)` — API 地址配置
- `electron.isElectron` — 标识 Electron 环境

### 渲染进程 (React SPA)

- 从 `html/react/user/src/` 迁移全部页面和组件
- `services/api.js` 修改：检测 Electron 环境时读取可配置的 API 地址
- 默认 API 地址：`http://localhost:8080/api`

### API 地址配置

- 默认值：`http://localhost:8080/api`
- 存储位置：Electron `app.getPath('userData')/config.json`
- 修改方式：菜单 → 文件 → 设置 → 弹窗输入新地址
- 生效方式：修改后提示重启应用

## 打包配置 (`electron-builder.yml`)

- 目标平台：Windows (NSIS 安装包)
- 应用 ID：`com.gcc.library1`
- 应用名称：图书管理系统
- 图标：默认 Electron 图标（可后续替换）
- 输出目录：`dist/`
- 构建命令：`npm run build`（Vite）→ `electron-builder --win`

## npm 脚本

```json
{
  "dev": "concurrently \"vite\" \"wait-on http://localhost:5173 && electron .\"",
  "build": "vite build && electron-builder --win",
  "build:vite": "vite build",
  "build:electron": "electron-builder --win",
  "preview": "vite preview"
}
```

## 依赖

### 运行依赖
- `react` ^19.2.0
- `react-dom` ^19.2.0
- `react-router-dom` ^7.9.6
- `bootstrap` ^5.3.8
- `react-bootstrap` ^2.10.10

### 开发依赖
- `vite` ^7.2.4
- `@vitejs/plugin-react` ^5.1.1
- `electron` ^35.0.0
- `electron-builder` ^26.0.0
- `concurrently` ^9.0.0（并运行 Vite + Electron）
- `wait-on` ^8.0.0（等待 Vite 启动后再启动 Electron）

## 与现有项目的关系

- 完全独立项目，独立的 `package.json` 和 `node_modules`
- 页面代码从 Web 版复制，少量适配修改
- 共享同一个 Spring Boot 后端 API
- Web 版不受影响

## 适配修改清单

从 Web 版迁移时需要修改的文件：

1. **`services/api.js`** — API 基础 URL 改为可配置，检测 `window.electron` 读取配置
2. **`App.jsx`** — 无需修改，路由逻辑完全复用
3. **所有页面组件** — 无需修改，功能完全复用
4. **新增文件**：
   - `electron/main.js` — 主进程
   - `electron/preload.js` — 预加载脚本
   - `electron-builder.yml` — 打包配置
   - `vite.config.js` — 新建，配置开发服务器
   - `index.html` — 新建，Vite 入口
   - `package.json` — 新建，包含所有依赖

## 验证标准

1. `npm run dev` 启动后 Electron 窗口正常显示
2. 登录、图书列表、借阅、历史、个人中心等所有页面正常工作
3. 管理员功能（用户管理、馆藏概览）正常
4. AI 推荐浮窗正常
5. 系统托盘最小化/恢复正常
6. 菜单 → 设置 → 修改 API 地址后重启生效
7. `npm run build` 生成 Windows `.exe` 安装包
