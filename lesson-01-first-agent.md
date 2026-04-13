# 第一课：手动体验 - 直接调用 LLM API

## 课程目标

- 理解什么是 Agent 及其与普通聊天机器人的区别
- 学会配置 Python 环境调用大语言模型 API
- 完成第一个 AI 程序：让 AI 和你对话
- 为后续课程打好基础

---

## 什么是 Agent？

> **简单理解**：Agent = 大脑（LLM）+ 工具（可以执行动作）+ 记忆（记住上下文）

| 聊天机器人 | Agent |
|-----------|-------|
| 只能聊天 | 能**执行任务** |
| 被动回答 | 能**主动规划** |
| 没有记忆 | 能**记住信息** |
| 无法联网 | 能**调用工具**（搜索、计算等） |

**例子**：
- 你问 ChatGPT："今天天气怎么样？" → 它说"我无法获取实时天气"
- 你问天气 Agent："今天天气怎么样？" → 它调用天气 API → 告诉你具体温度

---

## 课前准备

### 1. 硬件要求
- 一台可以上网的电脑

### 2. 软件要求
- 已安装 Python 3.8 或更高版本
- 一个代码编辑器（VS Code / PyCharm 都可以，甚至记事本也行）

### 3. 账号准备

**选择以下任一服务商注册，并获取 API Key：**

| 服务商 | 推荐理由 | 注册地址 | 费用 |
|--------|----------|----------|------|
| 智谱 AI | 有免费模型，学习首选 | https://open.bigmodel.cn | 免费 |
| DeepSeek | 价格便宜，性能强 | https://platform.deepseek.com | ¥1/百万tokens |
| 通义千问 | 阿里云出品，稳定 | https://dashscope.aliyuncs.com | 新用户免费 |

> **重点推荐智谱 AI**，因为它的 `glm-4-flash` 模型完全免费，适合学习！

### 获取 API Key 步骤（以智谱 AI 为例）

1. 打开 https://open.bigmodel.cn
2. 点击右上角「注册」，用手机号完成注册
3. 登录后，点击右上角头像 → 「API Key」
4. 点击「创建新的 API Key」
5. **复制并保存好这个 Key**（只显示一次，务必保存！）

---

## 环境搭建

### 步骤 1：检查 Python 是否安装

打开命令行（Windows 按 `Win + R` 输入 `cmd` 回车）：

```bash
python --version
```

如果显示类似 `Python 3.11.5`，说明已安装。

如果提示"不是内部或外部命令"，请先安装 Python：https://www.python.org/downloads/

### 步骤 2：安装 OpenAI 库

在命令行中执行：

```bash
pip install openai
```

等待安装完成即可。

---

## 实战：第一个 AI 程序

### 项目 1：最简单的对话程序

创建一个新文件 `hello_ai.py`，复制以下代码：

```python
# hello_ai.py
# 这是你的第一个 AI 程序！

from openai import OpenAI

# ========== 配置区域 ==========
# 把你自己的 API Key 填在这里（引号里面）
API_KEY = "你的APIKey填在这里"

# 智谱 AI 的配置
BASE_URL = "https://open.bigmodel.cn/api/v1"
MODEL = "glm-4-flash"  # 免费模型
# ==============================

# 创建客户端
client = OpenAI(
    api_key=API_KEY,
    base_url=BASE_URL
)

# 发送请求
print("正在向 AI 发送请求...")

response = client.chat.completions.create(
    model=MODEL,
    messages=[
        {"role": "system", "content": "你是一个友好的AI助手。"},
        {"role": "user", "content": "你好，请用一句话介绍一下你自己"}
    ]
)

# 获取 AI 的回复
reply = response.choices[0].message.content
print("\n=== AI 的回复 ===")
print(reply)
print("==================\n")
```

**运行程序**：
```bash
python hello_ai.py
```

你应该会看到类似这样的输出：
```
=== AI 的回复 ===
你好！我是一个由智谱AI开发的人工智能助手，我可以帮助你解答问题、提供信息和进行对话。
==================
```

---

### 项目 2：多轮对话程序

上面的程序只能问一次，现在让我们做一个能**连续对话**的版本。

创建新文件 `chat_bot.py`：

```python
# chat_bot.py
# 一个可以连续对话的聊天机器人

from openai import OpenAI

# ========== 配置区域 ==========
API_KEY = "你的APIKey填在这里"
BASE_URL = "https://open.bigmodel.cn/api/v1"
MODEL = "glm-4-flash"
# ==============================

client = OpenAI(api_key=API_KEY, base_url=BASE_URL)

# 存储对话历史
messages = [
    {"role": "system", "content": "你是一个友好的AI助手，名叫小智。"}
]

print("🤖 小智已启动！输入 'quit' 或 'exit' 退出\n")

while True:
    # 获取用户输入
    user_input = input("你：")

    # 检查是否要退出
    if user_input.lower() in ['quit', 'exit', '退出']:
        print("小智：再见！👋")
        break

    # 将用户消息添加到历史
    messages.append({"role": "user", "content": user_input})

    # 调用 AI
    response = client.chat.completions.create(
        model=MODEL,
        messages=messages
    )

    # 获取 AI 回复
    ai_reply = response.choices[0].message.content

    # 将 AI 回复也添加到历史（这样 AI 才能记住上下文）
    messages.append({"role": "assistant", "content": ai_reply})

    # 打印回复
    print(f"小智：{ai_reply}\n")
```

**运行并测试**：
```bash
python chat_bot.py
```

试着这样对话：
```
你：我叫小明
小智：你好小明！很高兴认识你，有什么我可以帮助你的吗？

你：我叫什么名字？
小智：你叫小明。是你刚才告诉我的。

你：我今年20岁
小智：好的，我知道了你今年20岁。

你：总结一下我的信息
小智：你叫小明，今年20岁。
```

> **关键点**：`messages` 列表就是 AI 的"记忆"，我们把每次对话都存进去，AI 才能记住之前说的内容。

---

### 项目 3：带 System Prompt 的专用助手

通过修改 System Prompt，我们可以让 AI 扮演不同角色。

创建新文件 `specialist.py`：

```python
# specialist.py
# 一个专用助手：英语学习教练

from openai import OpenAI

API_KEY = "你的APIKey填在这里"
BASE_URL = "https://open.bigmodel.cn/api/v1"
MODEL = "glm-4-flash"

client = OpenAI(api_key=API_KEY, base_url=BASE_URL)

# 这里的 system prompt 决定了 AI 的角色和能力
messages = [
    {
        "role": "system",
        "content": """你是一位专业的英语学习教练。

你的任务：
1. 帮助用户纠正英语语法错误
2. 用简单易懂的中文解释错误原因
3. 给出更地道的表达建议

回复格式：
❌ 原句：[用户的原句]
✅ 改正：[正确的句子]
💡 解释：[为什么错了/为什么这样更好]"""
    }
]

print("📚 英语教练已启动！输入你想说的英语，我会帮你纠正。\n")

while True:
    user_input = input("你的英语：")

    if user_input.lower() in ['quit', 'exit', '退出']:
        print("继续加油！Bye! 👋")
        break

    messages.append({"role": "user", "content": user_input})

    response = client.chat.completions.create(
        model=MODEL,
        messages=messages
    )

    ai_reply = response.choices[0].message.content
    messages.append({"role": "assistant", "content": ai_reply})

    print(f"\n{ai_reply}\n")
```

**测试示例**：
```
你的英语：I go to school yesterday.

❌ 原句：I go to school yesterday.
✅ 改正：I went to school yesterday.
💡 解释：因为 "yesterday" 表示过去的时间，所以动词要用过去式 "went" 而不是原形 "go"。
```

---

## 代码详解

### 消息格式

```python
messages = [
    {"role": "system", "content": "你是一个助手"},      # 设定角色
    {"role": "user", "content": "你好"},              # 用户说话
    {"role": "assistant", "content": "你好呀"}        # AI 的回复
]
```

| role | 作用 |
|------|------|
| `system` | 给 AI 设定身份和规则 |
| `user` | 用户的输入 |
| `assistant` | AI 之前的回复（用于多轮对话） |

### API 调用结构

```python
response = client.chat.completions.create(
    model="模型名称",
    messages=[...],           # 对话历史
    temperature=0.7,          # 可选：随机性（0-2，越大越随机）
    max_tokens=1000           # 可选：最大回复长度
)
```

---

## 练习题

### 必做（基础巩固）

1. **修改 System Prompt**，把聊天机器人变成一个"唐诗专家"，让它能背诵唐诗并解释含义。

2. **添加新功能**：在聊天程序中，每次显示 AI 回复时，同时显示本次对话用了多少 tokens（提示：查看 `response.usage`）。

### 选做（进阶挑战）

3. **创建一个"代码解释器"**：用户输入 Python 代码，AI 解释这段代码的作用。

4. **创建一个"面试助手"**：模拟面试官，向你提问你指定的职位相关问题，并给出反馈。

---

## 切换其他服务商

如果想换成 DeepSeek 或通义千问，只需修改配置：

```python
# DeepSeek 配置
API_KEY = "你的DeepSeek Key"
BASE_URL = "https://api.deepseek.com"
MODEL = "deepseek-chat"

# 通义千问配置
API_KEY = "你的通义Key"
BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1"
MODEL = "qwen-plus"
```

其他代码完全不用改！

---

## 常见问题

### Q1：运行时提示错误 "Incorrect API key provided"
- 检查 API_KEY 是否正确复制
- 确认没有多余的空格
- 确认 API Key 没有过期

### Q2：提示 "Connection error" 或连接超时
- 检查网络连接
- 尝试切换服务商
- 如果是校园网，可能需要设置代理

### Q3：返回的结果是英文，但我想要中文
- 在 system prompt 中明确说明："请用中文回答"

### Q4：如何节省费用？
- 使用智谱的免费模型 `glm-4-flash`
- 或使用 DeepSeek，价格很低（¥1/百万tokens）
- 控制 `max_tokens` 限制回复长度

---

## 本课小结

✅ 你学会了：
- 什么是 Agent，它和普通聊天机器人的区别
- 如何注册国内 AI 服务商并获取 API Key
- 如何使用 Python 调用大语言模型 API
- 如何实现多轮对话（通过维护 messages 列表）
- 如何通过 System Prompt 让 AI 扮演不同角色

## 下节课预告

**第二课：记忆与上下文**
- 学习更高级的记忆管理方式
- 实现一个"记住用户信息"的智能助手
- 了解对话窗口限制与应对方法

---

> 🎉 恭喜完成第一课！有任何问题随时提问。
