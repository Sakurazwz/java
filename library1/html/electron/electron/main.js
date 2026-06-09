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
let isQuitting = false

// 配置文件路径
const configPath = path.join(app.getPath('userData'), 'config.json')

// 读取配置（带默认值合并，防止配置文件缺少字段）
function loadConfig() {
  const defaults = { apiUrl: 'http://localhost:8080/api' }
  try {
    if (fs.existsSync(configPath)) {
      return { ...defaults, ...JSON.parse(fs.readFileSync(configPath, 'utf-8')) }
    }
  } catch (e) {
    console.error('读取配置失败:', e)
  }
  return defaults
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
    if (!isQuitting) {
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
  const iconPath = path.join(__dirname, '../assets/icon.png')
  const defaultIcon = path.join(__dirname, '../node_modules/electron/dist/electron.exe')

  try {
    tray = new Tray(fs.existsSync(iconPath) ? iconPath : defaultIcon)
  } catch (e) {
    console.error('创建托盘失败:', e)
    return
  }

  const contextMenu = Menu.buildFromTemplate([
    { label: '显示窗口', click: () => mainWindow?.show() },
    { type: 'separator' },
    { label: '退出', click: () => { isQuitting = true; app.quit() } },
  ])
  tray.setToolTip('图书管理系统')
  tray.setContextMenu(contextMenu)
  tray.on('double-click', () => mainWindow?.show())
}

// 设置 API 地址对话框（替代不存在的 dialog.showPrompt）
function showApiUrlDialog() {
  const config = loadConfig()
  const promptWindow = new BrowserWindow({
    width: 400,
    height: 200,
    parent: mainWindow,
    modal: true,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
    },
  })

  // 创建一个简单的 HTML 页面作为输入对话框
  const html = `
    <!DOCTYPE html>
    <html>
    <head>
      <style>
        body { font-family: system-ui; padding: 20px; }
        input { width: 100%; padding: 8px; margin: 10px 0; box-sizing: border-box; }
        button { padding: 8px 16px; margin-right: 10px; }
      </style>
    </head>
    <body>
      <h3>设置 API 地址</h3>
      <input type="url" id="apiUrl" value="${config.apiUrl}" placeholder="http://localhost:8080/api">
      <div>
        <button id="ok">确定</button>
        <button id="cancel">取消</button>
      </div>
      <script>
        const { ipcRenderer } = require('electron')
        document.getElementById('ok').addEventListener('click', () => {
          ipcRenderer.send('api-url-result', document.getElementById('apiUrl').value)
          window.close()
        })
        document.getElementById('cancel').addEventListener('click', () => {
          ipcRenderer.send('api-url-result', null)
          window.close()
        })
      </script>
    </body>
    </html>
  `

  promptWindow.loadURL(`data:text/html,${encodeURIComponent(html)}`)

  ipcMain.once('api-url-result', (event, value) => {
    if (value) {
      config.apiUrl = value
      saveConfig(config)
      dialog.showMessageBox(mainWindow, {
        type: 'info',
        title: '设置已保存',
        message: 'API 地址已更新，重启应用后生效。',
      })
    }
  })
}

// 创建应用菜单
function createMenu() {
  const menuTemplate = [
    {
      label: '文件',
      submenu: [
        {
          label: '设置 API 地址',
          click: () => showApiUrlDialog(),
        },
        { type: 'separator' },
        { label: '退出', click: () => { isQuitting = true; app.quit() } },
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
