# Electron 桌面端实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 创建基于 Electron 的 Windows 桌面客户端，复用现有 React 前端代码

**架构：** Electron 主进程管理窗口和系统托盘，预加载脚本通过 contextBridge 暴露安全 API，渲染进程运行 React SPA 通过 REST API 与后端通信

**技术栈：** Electron 35、React 19、Vite 7、React-Bootstrap 5

---

## 文件结构

### 新建文件
- `html/electron/package.json` - 项目配置和依赖
- `html/electron/electron/main.js` - Electron 主进程
- `html/electron/electron/preload.js` - 预加载脚本
- `html/electron/vite.config.js` - Vite 配置
- `html/electron/index.html` - Vite 入口 HTML
- `html/electron/electron-builder.yml` - 打包配置

### 迁移文件（从 `html/react/user/src/` 复制）
- `src/main.jsx` - React 入口
- `src/App.jsx` - 路由定义
- `src/App.css` - 全局样式
- `src/components/AiRecommend.jsx` + `.css`
- `src/pages/user/header/Header.jsx` + `.css`
- `src/pages/user/login/login.jsx` + `.css`
- `src/pages/user/adduser/adduser.jsx` + `.css`
- `src/pages/user/books/books.jsx` + `.css`
- `src/pages/user/borrow/borrow.jsx` + `.css`
- `src/pages/user/history/history.jsx` + `.css`
- `src/pages/user/profile/profile.jsx` + `.css`
- `src/pages/user/users/users.jsx` + `.css`
- `src/pages/user/overview/overview.jsx` + `.css`

### 修改文件
- `src/services/api.js` - 支持可配置 API 地址

---

## 任务 1：创建项目结构和 package.json

**文件：**
- 创建：`html/electron/package.json`

- [ ] **步骤 1：创建目录结构**

```bash
mkdir -p html/electron/electron
mkdir -p html/electron/src
```

- [ ] **步骤 2：创建 package.json**

```json
{
  "name": "library1-electron",
  "private": true,
  "version": "1.0.0",
  "description": "图书管理系统桌面客户端",
  "main": "electron/main.js",
  "type": "module",
  "scripts": {
    "dev": "concurrently \"vite\" \"wait-on http://localhost:5173 && electron .\"",
    "build": "vite build && electron-builder --win",
    "build:vite": "vite build",
    "build:electron": "electron-builder --win",
    "preview": "vite preview"
  },
  "dependencies": {
    "bootstrap": "^5.3.8",
    "react": "^19.2.0",
    "react-bootstrap": "^2.10.10",
    "react-dom": "^19.2.0",
    "react-router-dom": "^7.9.6"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^5.1.1",
    "concurrently": "^9.0.0",
    "electron": "^35.0.0",
    "electron-builder": "^26.0.0",
    "vite": "^7.2.4",
    "wait-on": "^8.0.0"
  }
}
```

- [ ] **步骤 3：Commit**

```bash
git add html/electron/package.json
git commit -m "feat(electron): 初始化项目结构和 package.json"
```

---

## 任务 2：创建 Electron 主进程

**文件：**
- 创建：`html/electron/electron/main.js`

- [ ] **步骤 1：创建 main.js**

```javascript
import { app, BrowserWindow, Menu, Tray, dialog, ipcMain } from 'electron'
import path from 'path'
import { fileURLToPath } from 'url'
import fs from 'fs'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 单实例锁
const gotTheLock = app.requestSingleInstanceLock()
if (!gotTheLock) {
  app.quit()
}

let mainWindow = null
let tray = null

// 配置文件路径
const configPath = path.join(app.getPath('userData'), 'config.json')

// 读取配置
function loadConfig() {
  try {
    if (fs.existsSync(configPath)) {
      return JSON.parse(fs.readFileSync(configPath, 'utf-8'))
    }
  } catch (e) {
    console.error('读取配置失败:', e)
  }
  return { apiUrl: 'http://localhost:8080/api' }
}

// 保存配置
function saveConfig(config) {
  try {
    fs.writeFileSync(configPath, JSON.stringify(config, null, 2))
  } catch (e) {
    console.error('保存配置失败:', e)
  }
}

// 创建主窗口
function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
    },
  })

  // 开发环境加载 Vite 开发服务器，生产环境加载构建产物
  if (process.env.NODE_ENV === 'development' || !app.isPackaged) {
    mainWindow.loadURL('http://localhost:5173')
    mainWindow.webContents.openDevTools()
  } else {
    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'))
  }

  // 窗口关闭时最小化到托盘
  mainWindow.on('close', (event) => {
    if (!app.isQuitting) {
      event.preventDefault()
      mainWindow.hide()
    }
  })

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

// 创建系统托盘
function createTray() {
  tray = new Tray(path.join(__dirname, '../assets/icon.png'))
  const contextMenu = Menu.buildFromTemplate([
    { label: '显示窗口', click: () => mainWindow.show() },
    { type: 'separator' },
    { label: '退出', click: () => { app.isQuitting = true; app.quit() } },
  ])
  tray.setToolTip('图书管理系统')
  tray.setContextMenu(contextMenu)
  tray.on('double-click', () => mainWindow.show())
}

// 创建应用菜单
function createMenu() {
  const config = loadConfig()
  const menuTemplate = [
    {
      label: '文件',
      submenu: [
        {
          label: '设置 API 地址',
          click: async () => {
            const { value } = await dialog.showPrompt(mainWindow, {
              title: '设置 API 地址',
              label: 'API 地址:',
              value: config.apiUrl,
              inputAttrs: { type: 'url' },
            })
            if (value) {
              config.apiUrl = value
              saveConfig(config)
              dialog.showMessageBox(mainWindow, {
                type: 'info',
                title: '设置已保存',
                message: 'API 地址已更新，重启应用后生效。',
              })
            }
          },
        },
        { type: 'separator' },
        { label: '退出', click: () => { app.isQuitting = true; app.quit() } },
      ],
    },
    {
      label: '视图',
      submenu: [
        { role: 'zoomIn' },
        { role: 'zoomOut' },
        { role: 'resetZoom' },
        { type: 'separator' },
        { role: 'toggleDevTools' },
        { type: 'separator' },
        { role: 'reload' },
      ],
    },
    {
      label: '帮助',
      submenu: [
        {
          label: '关于',
          click: () => {
            dialog.showMessageBox(mainWindow, {
              type: 'info',
              title: '关于',
              message: '图书管理系统',
              detail: `版本: ${app.getVersion()}\n基于 Electron 构建`,
            })
          },
        },
      ],
    },
  ]
  Menu.setApplicationMenu(Menu.buildFromTemplate(menuTemplate))
}

// IPC 处理
ipcMain.handle('get-app-version', () => app.getVersion())
ipcMain.handle('get-api-url', () => loadConfig().apiUrl)
ipcMain.handle('set-api-url', (event, url) => {
  const config = loadConfig()
  config.apiUrl = url
  saveConfig(config)
})

// 应用就绪
app.whenReady().then(() => {
  createWindow()
  createTray()
  createMenu()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

// 所有窗口关闭
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// 第二个实例尝试启动
app.on('second-instance', () => {
  if (mainWindow) {
    if (mainWindow.isMinimized()) mainWindow.restore()
    mainWindow.show()
  }
})
```

- [ ] **步骤 2：Commit**

```bash
git add html/electron/electron/main.js
git commit -m "feat(electron): 实现主进程，包含窗口管理、托盘和菜单"
```

---

## 任务 3：创建预加载脚本

**文件：**
- 创建：`html/electron/electron/preload.js`

- [ ] **步骤 1：创建 preload.js**

```javascript
import { contextBridge, ipcRenderer } from 'electron'

// 通过 contextBridge 安全暴露 API
contextBridge.exposeInMainWorld('electron', {
  // 获取应用版本
  getAppVersion: () => ipcRenderer.invoke('get-app-version'),

  // 获取 API 地址
  getApiUrl: () => ipcRenderer.invoke('get-api-url'),

  // 设置 API 地址
  setApiUrl: (url) => ipcRenderer.invoke('set-api-url', url),

  // 标识 Electron 环境
  isElectron: true,
})
```

- [ ] **步骤 2：Commit**

```bash
git add html/electron/electron/preload.js
git commit -m "feat(electron): 实现预加载脚本，暴露安全 API"
```

---

## 任务 4：创建 Vite 配置和入口文件

**文件：**
- 创建：`html/electron/vite.config.js`
- 创建：`html/electron/index.html`

- [ ] **步骤 1：创建 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: './',
  server: {
    port: 5173,
    strictPort: true,
  },
  build: {
    outDir: 'dist',
  },
})
```

- [ ] **步骤 2：创建 index.html**

```html
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>图书管理系统</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

- [ ] **步骤 3：Commit**

```bash
git add html/electron/vite.config.js html/electron/index.html
git commit -m "feat(electron): 创建 Vite 配置和入口 HTML"
```

---

## 任务 5：创建打包配置

**文件：**
- 创建：`html/electron/electron-builder.yml`

- [ ] **步骤 1：创建 electron-builder.yml**

```yaml
appId: com.gcc.library1
productName: 图书管理系统
directories:
  output: dist
files:
  - electron/**/*
  - dist/**/*
  - package.json
win:
  target: nsis
nsis:
  oneClick: false
  allowToChangeInstallationDirectory: true
```

- [ ] **步骤 2：Commit**

```bash
git add html/electron/electron-builder.yml
git commit -m "feat(electron): 添加 electron-builder 打包配置"
```

---

## 任务 6：迁移前端代码

**文件：**
- 复制：`html/react/user/src/` → `html/electron/src/`

- [ ] **步骤 1：复制所有前端文件**

```bash
# 复制目录结构
cp -r html/react/user/src/* html/electron/src/

# 确保目录结构完整
ls -la html/electron/src/
ls -la html/electron/src/pages/user/
ls -la html/electron/src/components/
ls -la html/electron/src/services/
```

- [ ] **步骤 2：验证文件完整性**

检查以下文件是否存在：
- `src/main.jsx`
- `src/App.jsx`
- `src/App.css`
- `src/components/AiRecommend.jsx`
- `src/services/api.js`
- `src/pages/user/header/Header.jsx`
- `src/pages/user/login/login.jsx`
- `src/pages/user/adduser/adduser.jsx`
- `src/pages/user/books/books.jsx`
- `src/pages/user/borrow/borrow.jsx`
- `src/pages/user/history/history.jsx`
- `src/pages/user/profile/profile.jsx`
- `src/pages/user/users/users.jsx`
- `src/pages/user/overview/overview.jsx`

- [ ] **步骤 3：Commit**

```bash
git add html/electron/src/
git commit -m "feat(electron): 迁移前端代码到 Electron 项目"
```

---

## 任务 7：修改 api.js 支持可配置 API 地址

**文件：**
- 修改：`html/electron/src/services/api.js`

- [ ] **步骤 1：修改 api.js**

将原有的硬编码 API 地址改为动态获取：

```javascript
// 全局 API 服务，封装所有后端接口调用
// 支持 Electron 环境下可配置的 API 地址
let API_BASE_URL = "http://localhost:8080/api"

// 初始化 API 地址（Electron 环境下从主进程获取）
const initApiUrl = async () => {
  if (window.electron && window.electron.isElectron) {
    try {
      API_BASE_URL = await window.electron.getApiUrl()
    } catch (e) {
      console.error("获取 API 地址失败:", e)
    }
  }
}

// 立即初始化
initApiUrl()

// 获取当前 API 地址
export const getApiBaseUrl = () => API_BASE_URL

// 获取 JWT Token
const getToken = () => localStorage.getItem("token")

// 设置 JWT Token
const setToken = (token) => {
    localStorage.setItem("token", token)
    // 解析并保存用户角色
    const payload = parseJwt(token)
    if (payload && payload.role) {
        localStorage.setItem("userRole", payload.role)
    }
}

// 解析 JWT payload
const parseJwt = (token) => {
    if (!token) return null
    try {
        const base64Url = token.split(".")[1]
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split("")
                .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
                .join("")
        )
        return JSON.parse(jsonPayload)
    } catch (e) {
        console.error("JWT解析失败:", e)
        return null
    }
}

// 从 JWT 获取当前用户信息（避免各组件重复解析）
const getCurrentUser = () => {
    const token = getToken()
    if (!token) return null
    const payload = parseJwt(token)
    if (!payload) return null
    return { id: payload.userId, name: payload.sub, role: payload.role }
}

// 获取用户角色
const getRole = () => localStorage.getItem("userRole")

// 判断是否为管理员
const isAdmin = () => {
    const role = getRole()
    return role === "ADMIN" || role === "ROLE_ADMIN"
}

// 清除 JWT Token
const removeToken = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("userRole")
}

// 请求拦截器
const fetchWithAuth = async (url, options = {}) => {
    const token = getToken()
    const headers = {
        "Content-Type": "application/json",
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
    }

    const response = await fetch(url, { ...options, headers })

    // 如果返回 401 或 403，跳转登录页
    if (response.status === 401 || response.status === 403) {
        removeToken()
        window.location.href = "/login"
        throw new Error("登录已过期，请重新登录")
    }

    return response
}

// 用户相关 API
export const authApi = {
    login: async (name, password) => {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: name, password }),
        })
        if (response.ok) {
            const data = await response.json()
            if (data.token) {
                setToken(data.token)
            }
            return data
        }
        throw new Error("登录失败")
    },

    logout: () => {
        removeToken()
    },

    isAuthenticated: () => !!getToken(),

    // 获取用户角色
    getRole,

    // 判断是否为管理员
    isAdmin,

    getCurrentUser,

    getToken,
    setToken,
    removeToken,
}

// 用户管理 API
export const userApi = {
    register: async (name, password) => {
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, password }),
        })
        if (!response.ok) {
            const error = await response.text()
            throw new Error(error)
        }
        return response.json()
    },

    getAllUsers: async (name) => {
        const url = name
            ? `${API_BASE_URL}/users/all?name=${encodeURIComponent(name)}`
            : `${API_BASE_URL}/users/all`
        const response = await fetchWithAuth(url)
        if (!response.ok) throw new Error("获取用户列表失败")
        return response.json()
    },

    deleteUser: async (id) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/users/delete/${id}`, {
            method: "DELETE",
        })
        if (!response.ok) {
            const error = await response.text()
            throw new Error(error)
        }
        return response.text()
    },

    updateRole: async (id, role) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/users/updateRole/${id}`, {
            method: "PUT",
            body: JSON.stringify({ role }),
        })
        if (!response.ok) {
            const error = await response.text()
            throw new Error(error)
        }
        return response.json()
    },
}

// 图书管理 API
export const bookApi = {
    getAllBooks: async () => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getAllBooks`)
        if (!response.ok) throw new Error("获取图书列表失败")
        return response.json()
    },

    getBookById: async (id) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getBookById/${id}`)
        if (!response.ok) throw new Error("获取图书信息失败")
        return response.json()
    },

    searchBooks: async (title) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getBookByTitle?title=${encodeURIComponent(title)}`)
        if (!response.ok) throw new Error("搜索失败")
        return response.json()
    },

    getBooksByIsbn: async (isbn) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getBooksByIsbn?isbn=${encodeURIComponent(isbn)}`)
        if (!response.ok) throw new Error("搜索ISBN失败")
        return response.json()
    },

    getCopyCountByIsbn: async (isbn) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getCopyCountByIsbn?isbn=${encodeURIComponent(isbn)}`)
        if (!response.ok) return 0
        return response.json()
    },

    addBook: async (book) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/addBook`, {
            method: "POST",
            body: JSON.stringify(book),
        })
        if (!response.ok) throw new Error("添加图书失败")
        return response.json()
    },

    updateBook: async (book) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/updateBook`, {
            method: "PUT",
            body: JSON.stringify(book),
        })
        if (!response.ok) throw new Error("更新图书失败")
        return response.json()
    },

    deleteBook: async (id) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/deleteBook/${id}`, {
            method: "DELETE",
        })
        if (!response.ok) throw new Error("删除图书失败")
        return response.text()
    },

    recommend: async (query) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/recommend?query=${encodeURIComponent(query)}`)
        if (!response.ok) throw new Error("推荐请求失败")
        return response.json()
    },
}

// 借阅管理 API
export const borrowApi = {
    getAllBorrows: async ({ userId } = {}) => {
        const params = new URLSearchParams()
        if (userId) params.append("userId", userId)
        const qs = params.toString()
        const url = `${API_BASE_URL}/borrow/all${qs ? "?" + qs : ""}`
        const response = await fetchWithAuth(url)
        if (!response.ok) throw new Error("获取借阅记录失败")
        return response.json()
    },

    borrowBook: async (bookId, userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/add`, {
            method: "POST",
            body: JSON.stringify({ bookId, userId }),
        })
        if (!response.ok) throw new Error("借书失败")
        return response.text()
    },

    returnBook: async (bookId, userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/back`, {
            method: "DELETE",
            body: JSON.stringify({ bookId, userId }),
        })
        if (!response.ok) throw new Error("还书失败")
        return response.text()
    },

    renewBook: async (bookId, userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/updateBorrow`, {
            method: "POST",
            body: JSON.stringify({ bookId, userId }),
        })
        if (!response.ok) throw new Error("续借失败")
        return response.text()
    },

    getUserBorrows: async (userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/user?userId=${userId}`)
        if (!response.ok) throw new Error("获取用户借阅记录失败")
        return response.json()
    },

    getOverdueBorrows: async (userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/overdue?userId=${userId}`)
        if (!response.ok) throw new Error("获取逾期记录失败")
        return response.json()
    },
}

// 借阅历史 API
export const historyApi = {
    getAllHistory: async ({ userId, startDate, endDate } = {}) => {
        const params = new URLSearchParams()
        if (userId) params.append("userId", userId)
        if (startDate) params.append("startDate", startDate)
        if (endDate) params.append("endDate", endDate)
        const qs = params.toString()
        const url = `${API_BASE_URL}/borrowhistory/all${qs ? "?" + qs : ""}`
        const response = await fetchWithAuth(url)
        if (!response.ok) throw new Error("获取全部历史记录失败")
        return response.json()
    },

    getHistoryByBookId: async (bookId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrowhistory/getBorrowHistoryByBookId/${bookId}`)
        if (!response.ok) throw new Error("获取历史记录失败")
        return response.json()
    },

    getHistoryByUserId: async (userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrowhistory/getBorrowHistoryByUserId/${userId}`)
        if (!response.ok) throw new Error("获取用户历史记录失败")
        return response.json()
    },
}

export default { authApi, userApi, bookApi, borrowApi, historyApi, getApiBaseUrl }
```

- [ ] **步骤 2：Commit**

```bash
git add html/electron/src/services/api.js
git commit -m "feat(electron): 修改 api.js 支持可配置 API 地址"
```

---

## 任务 8：安装依赖并测试开发环境

**文件：**
- 无（仅命令操作）

- [ ] **步骤 1：安装依赖**

```bash
cd html/electron
npm install
```

- [ ] **步骤 2：测试开发服务器**

```bash
npm run dev
```

预期：Electron 窗口正常显示，可以访问登录页面

- [ ] **步骤 3：测试基本功能**

1. 登录功能正常
2. 图书列表显示正常
3. 借阅功能正常
4. 历史记录正常
5. 个人中心正常
6. AI 推荐浮窗正常

- [ ] **步骤 4：Commit**

```bash
git add html/electron/package-lock.json
git commit -m "chore(electron): 安装依赖并验证开发环境"
```

---

## 任务 9：测试生产构建

**文件：**
- 无（仅命令操作）

- [ ] **步骤 1：构建生产版本**

```bash
cd html/electron
npm run build
```

预期：在 `dist/` 目录生成 `.exe` 安装包

- [ ] **步骤 2：测试安装包**

运行生成的 `.exe` 文件，验证：
1. 应用正常启动
2. 系统托盘功能正常
3. 菜单功能正常
4. 所有页面功能正常

- [ ] **步骤 3：Commit**

```bash
git add html/electron/dist/
git commit -m "chore(electron): 完成生产构建测试"
```

---

## 自检清单

1. **规格覆盖度：**
   - [x] Electron 主进程（窗口管理、托盘、菜单）
   - [x] 预加载脚本（contextBridge API）
   - [x] React SPA 迁移
   - [x] 可配置 API 地址
   - [x] Vite 配置
   - [x] 打包配置
   - [x] 依赖管理

2. **占位符扫描：**
   - [x] 无 "待定"、"TODO" 占位符
   - [x] 所有代码块完整
   - [x] 所有命令明确

3. **类型一致性：**
   - [x] API 函数签名与原项目一致
   - [x] 组件导入路径正确

4. **验证标准覆盖：**
   - [x] `npm run dev` 启动测试
   - [x] 所有页面功能测试
   - [x] 系统托盘测试
   - [x] 菜单设置测试
   - [x] `npm run build` 构建测试

---

## 执行方式

计划已完成并保存到 `docs/superpowers/plans/2026-06-09-electron-desktop-implementation.md`。

**两种执行方式：**

**1. 子代理驱动（推荐）** - 每个任务调度一个新的子代理，任务间进行审查，快速迭代

**2. 内联执行** - 在当前会话中使用 executing-plans 执行任务，批量执行并设有检查点

选哪种方式？
