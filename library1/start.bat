@echo off
chcp 65001 >nul
setlocal

set "JAVA_HOME=C:\Users\Xian\.jdks\ms-21.0.10"

echo 正在启动 Ollama 服务...
start "Ollama DeepSeek" cmd /k "ollama run deepseek-r1:7b"

echo 等待 Ollama 初始化...
timeout /t 5 /nobreak >nul

echo 正在启动后端服务...
cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause