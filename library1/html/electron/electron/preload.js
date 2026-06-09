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
