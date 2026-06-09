import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import { HashRouter } from 'react-router-dom'
import { initApiUrl } from './services/api.js'

// 等待 API 初始化完成后再渲染，避免 Electron 环境下的竞态条件
const renderApp = async () => {
  await initApiUrl()

  createRoot(document.getElementById('root')).render(
    <StrictMode>
      <HashRouter>
        <App />
      </HashRouter>
    </StrictMode>,
  )
}

// 用户端入口
renderApp()
