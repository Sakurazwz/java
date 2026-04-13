# 第二课：记忆与上下文管理

## 课程目标

- 理解对话窗口限制和 Token 的概念
- 掌握常见的记忆管理策略
- 学会实现"记住用户信息"的智能助手
- 能够持久化保存和恢复对话历史

---

## 课前回顾

在第一课中，我们学会了：
- ✅ 调用 LLM API 进行对话
- ✅ 通过维护 `messages` 列表实现多轮对话
- ✅ 使用 System Prompt 设定 AI 角色

**问题**：当对话越来越长，会发生什么？

---

## 一、为什么需要记忆管理？

### 1.1 对话窗口限制

大语言模型有一个"上下文窗口"（Context Window），限制了它能处理的文本长度：

| 模型 | 上下文窗口 |
|------|-----------|
| GPT-4 | 8K / 32K / 128K tokens |
| Claude 3 | 200K tokens |
| DeepSeek | 32K / 128K tokens |
| 智谱 GLM-4 | 128K tokens |

> **1 Token ≈ 0.75 个中文字 ≈ 0.4 个英文单词**

### 1.2 超出窗口会怎样？

```python
# 对话太长时的表现
messages = [
    # ... 超过模型的上下文窗口
]

response = client.chat.completions.create(
    model="xxx",
    messages=messages  # ❌ 可能报错或截断
)
```

**常见错误**：
- `"This model's maximum context length is X tokens"`
- API 调用失败
- AI "忘记"了早期对话内容

### 1.3 成本问题

即使模型支持长窗口，每次调用都要发送完整的对话历史：
- 对话越长 → Token 越多 → 费用越高
- 每 1000 tokens 输入 ≈ DeepSeek ¥0.001

---

## 二、Token 计算基础

### 2.1 获取 Token 使用情况

```python
response = client.chat.completions.create(
    model=MODEL,
    messages=messages
)

# 查看 token 使用
if response.usage:
    print(f"输入: {response.usage.prompt_tokens}")
    print(f"输出: {response.usage.completion_tokens}")
    print(f"总计: {response.usage.total_tokens}")
```

### 2.2 估算文本长度

```python
# 简单估算函数
def estimate_tokens(text: str) -> int:
    """粗略估算中英文混合文本的 token 数"""
    # 中文约 1.5 tokens/字，英文约 0.3 tokens/词
    chinese_chars = sum(1 for c in text if '\u4e00' <= c <= '\u9fff')
    english_chars = len(text) - chinese_chars
    return int(chinese_chars * 1.5 + english_chars * 0.3)

text = "你好，这是一个测试。Hello, this is a test."
print(f"估算 tokens: {estimate_tokens(text)}")
```

### 2.3 精确计算（使用 tiktoken）

```bash
pip install tiktoken
```

```python
import tiktoken

def count_tokens(text: str, model: str = "gpt-3.5-turbo") -> int:
    """精确计算 tokens"""
    try:
        encoding = tiktoken.encoding_for_model(model)
    except KeyError:
        encoding = tiktoken.get_encoding("cl100k_base")
    return len(encoding.encode(text))

# 计算整个对话历史的 tokens
def count_messages_tokens(messages: list) -> int:
    """计算 messages 列表的总 tokens"""
    total = 0
    for msg in messages:
        total += count_tokens(msg.get("content", ""))
    return total
```

---

## 三、记忆管理策略

### 策略对比

| 策略 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| **保留最近 N 轮** | 简单有效 | 会丢失早期信息 | 短期对话 |
| **滑动窗口** | 平衡性能和记忆 | 可能丢失关键信息 | 通用场景 |
| **智能总结** | 保留关键信息 | 需要额外 API 调用 | 长期对话 |
| **语义检索** | 精准召回 | 需要向量数据库 | 知识库型应用 |
| **混合策略** | 兼顾各种需求 | 实现复杂 | 高级应用 |

### 3.1 策略一：保留最近 N 轮

```python
def keep_recent_messages(messages: list, max_turns: int = 10) -> list:
    """只保留最近 N 轮对话"""
    # 始终保留 system 消息
    system_msgs = [m for m in messages if m["role"] == "system"]
    # 获取最近 N 轮（每轮 = user + assistant）
    recent_msgs = messages[-(max_turns * 2):]

    return system_msgs + recent_msgs

# 使用示例
messages = [...]  # 很长的对话历史
messages = keep_recent_messages(messages, max_turns=5)
```

### 3.2 策略二：滑动窗口 + Token 限制

```python
def trim_by_tokens(messages: list, max_tokens: int = 4000) -> list:
    """按 token 数量裁剪对话历史"""
    system_msgs = [m for m in messages if m["role"] == "system"]
    other_msgs = [m for m in messages if m["role"] != "system"]

    result = system_msgs.copy()
    current_tokens = sum(count_messages_tokens([m]) for m in system_msgs)

    # 从最新消息开始倒序添加
    for msg in reversed(other_msgs):
        msg_tokens = count_messages_tokens([msg])
        if current_tokens + msg_tokens > max_tokens:
            break
        result.insert(len(system_msgs), msg)
        current_tokens += msg_tokens

    return result
```

### 3.3 策略三：智能总结

```python
def summarize_old_messages(messages: list, client, model: str) -> list:
    """将旧对话总结后保留"""
    # 分离 system、旧消息、新消息
    system_msgs = [m for m in messages if m["role"] == "system"]

    # 假设保留最近 5 轮，其余的总结
    keep_turns = 5
    if len(messages) <= keep_turns * 2 + len(system_msgs):
        return messages

    recent_msgs = messages[-(keep_turns * 2):]
    old_msgs = messages[len(system_msgs):-keep_turns * 2]

    # 调用 AI 总结
    summary_prompt = f"""请将以下对话历史总结成一段简洁的摘要，
保留关键信息（如人名、事件、偏好等）：

{chr(10).join(f'{m["role"]}: {m["content"]}' for m in old_msgs)}
"""

    summary_response = client.chat.completions.create(
        model=model,
        messages=[{"role": "user", "content": summary_prompt}]
    )
    summary = summary_response.choices[0].message.content

    # 构建新的 messages
    return system_msgs + [
        {"role": "system", "content": f"以下是之前对话的摘要：{summary}"}
    ] + recent_msgs
```

---

## 四、实战项目一：有记忆的聊天助手

创建文件 `memory_bot.py`：

```python
# memory_bot.py
# 一个能记住用户偏好的智能聊天助手

from openai import OpenAI
import json
import os
from datetime import datetime

# ========== 配置区域 ==========
API_KEY = "你的APIKey填在这里"
BASE_URL = "https://open.bigmodel.cn/api/v1"
MODEL = "glm-4-flash"
MEMORY_FILE = "user_memory.json"  # 记忆存储文件
MAX_TURNS = 10  # 保留最近对话轮数
MAX_TOKENS = 3000  # 最大 token 限制
# ==============================

client = OpenAI(api_key=API_KEY, base_url=BASE_URL)

def load_memory() -> dict:
    """加载用户记忆"""
    if os.path.exists(MEMORY_FILE):
        with open(MEMORY_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    return {
        "user_name": None,
        "preferences": {},
        "facts_learned": [],
        "conversation_count": 0
    }

def save_memory(memory: dict):
    """保存用户记忆"""
    with open(MEMORY_FILE, "w", encoding="utf-8") as f:
        json.dump(memory, f, ensure_ascii=False, indent=2)

def extract_user_info(user_input: str, ai_response: str) -> dict:
    """从对话中提取用户信息"""
    extract_prompt = f"""从以下对话中提取用户的个人信息和偏好。
如果对话中没有新信息，返回空对象 {{}}。

对话：
用户：{user_input}
助手：{ai_response}

请以 JSON 格式返回，包含以下字段（如果有）：
- user_name: 用户名字
- preferences: 偏好字典，如 {{"favorite_color": "蓝色", "hobby": "读书"}}
- facts: 学到的事实，如 ["住在上海", "是程序员"]

只返回 JSON，不要其他内容。"""

    try:
        response = client.chat.completions.create(
            model=MODEL,
            messages=[{"role": "user", "content": extract_prompt}],
            temperature=0
        )
        result = response.choices[0].message.content
        # 清理可能出现的 markdown 代码块标记
        result = result.strip()
        if result.startswith("```"):
            result = result.split("```")[1]
            if result.startswith("json"):
                result = result[4:]
        return json.loads(result)
    except:
        return {}

def update_memory(memory: dict, new_info: dict):
    """更新记忆"""
    if "user_name" in new_info and new_info["user_name"]:
        memory["user_name"] = new_info["user_name"]

    if "preferences" in new_info:
        memory["preferences"].update(new_info["preferences"])

    if "facts" in new_info:
        memory["facts_learned"].extend(new_info["facts"])
        # 保持事实列表不超过 20 条
        if len(memory["facts_learned"]) > 20:
            memory["facts_learned"] = memory["facts_learned"][-20:]

    memory["conversation_count"] += 1
    save_memory(memory)

def build_memory_context(memory: dict) -> str:
    """构建记忆上下文"""
    parts = []
    if memory.get("user_name"):
        parts.append(f"用户的名字是：{memory['user_name']}")

    if memory.get("preferences"):
        prefs = ", ".join(f"{k}={v}" for k, v in memory["preferences"].items())
        parts.append(f"用户偏好：{prefs}")

    if memory.get("facts_learned"):
        facts = "; ".join(memory["facts_learned"][-10:])  # 最近 10 条
        parts.append(f"已知信息：{facts}")

    parts.append(f"已进行对话 {memory.get('conversation_count', 0)} 次")

    return "\n".join(parts) if parts else "这是第一次与用户对话"

def trim_messages(messages: list, max_tokens: int = MAX_TOKENS) -> list:
    """按 token 限制裁剪消息"""
    # 简单实现：保留最近的对话
    system_msgs = [m for m in messages if m["role"] == "system"]
    other_msgs = [m for m in messages if m["role"] != "system"]

    # 从最新开始，估算 tokens（简化计算）
    result = system_msgs.copy()
    current_length = sum(len(m.get("content", "")) for m in system_msgs)

    for msg in reversed(other_msgs):
        msg_length = len(msg.get("content", ""))
        if current_length + msg_length > max_tokens * 2:  # 粗略估算
            break
        result.insert(len(system_msgs), msg)
        current_length += msg_length

    return result

def main():
    # 加载记忆
    memory = load_memory()

    # 构建带记忆的 system prompt
    memory_context = build_memory_context(memory)

    messages = [
        {
            "role": "system",
            "content": f"""你是一个友好的AI助手，名字叫小忆。
你有一个特殊能力：你能记住关于用户的信息。

{memory_context}

在对话中，自然地运用你对用户的了解。
如果用户告诉你新的个人信息，请温和地确认你记住了。
"""
        }
    ]

    print("=" * 50)
    print("        🧠 小忆 - 有记忆的聊天助手")
    print("=" * 50)

    if memory.get("user_name"):
        print(f"\n👋 欢迎回来，{memory['user_name']}！")
        print(f"📊 我们已经聊了 {memory['conversation_count']} 次了\n")
    else:
        print("\n👋 你好！我是小忆，我会记住你告诉我的事情哦\n")

    while True:
        user_input = input("你：")

        if user_input.lower() in ['quit', 'exit', '退出']:
            print(f"\n小忆：再见！{memory.get('user_name') or '朋友'}！我会记住你的。👋")
            break

        if user_input.lower() in ['记忆', 'memory', '记得什么']:
            print(f"\n🧠 我记得：")
            print(f"  名字：{memory.get('user_name', '还不知道')}")
            print(f"  偏好：{memory.get('preferences', {})}")
            print(f"  信息：{'; '.join(memory.get('facts_learned', []) or ['还没有'])}\n")
            continue

        # 添加用户消息
        messages.append({"role": "user", "content": user_input})

        # 调用 AI
        response = client.chat.completions.create(
            model=MODEL,
            messages=messages
        )

        ai_reply = response.choices[0].message.content
        messages.append({"role": "assistant", "content": ai_reply})

        print(f"\n小忆：{ai_reply}\n")

        # 提取并保存新信息
        new_info = extract_user_info(user_input, ai_reply)
        if new_info:
            update_memory(memory, new_info)

        # 裁剪消息历史
        messages = trim_messages(messages)

        # 显示 token 使用（如果支持）
        if response.usage:
            print(f"💡 本次对话：{response.usage.total_tokens} tokens")

if __name__ == "__main__":
    main()
```

**运行测试**：

```bash
python lesson1/memory_bot.py
```

**试试这样对话**：

```
你：我叫小明
小忆：你好小明！很高兴认识你...
你：我喜欢蓝色，还有打篮球
小忆：我记住了，你喜欢蓝色和打篮球...
你：我住在北京
小忆：好的，我记住了你住在北京...
你：记得什么
🧠 我记得：
  名字：小明
  偏好：{'favorite_color': '蓝色', 'hobby': '打篮球'}
  信息：住在北京
你：退出
小忆：再见！小明！我会记住你的。👋
```

再次运行程序，小忆会记得你！

---

## 五、实战项目二：长期记忆助手

创建文件 `persistent_chat.py`：

```python
# persistent_chat.py
# 支持会话保存和恢复的聊天助手

from openai import OpenAI
import json
import os
from datetime import datetime

API_KEY = "你的APIKey填在这里"
BASE_URL = "https://open.bigmodel.cn/api/v1"
MODEL = "glm-4-flash"

SESSIONS_DIR = "chat_sessions"  # 会话保存目录

client = OpenAI(api_key=API_KEY, base_url=BASE_URL)

def ensure_sessions_dir():
    """确保会话目录存在"""
    if not os.path.exists(SESSIONS_DIR):
        os.makedirs(SESSIONS_DIR)

def list_sessions() -> list:
    """列出所有保存的会话"""
    ensure_sessions_dir()
    sessions = []
    for filename in os.listdir(SESSIONS_DIR):
        if filename.endswith(".json"):
            filepath = os.path.join(SESSIONS_DIR, filename)
            with open(filepath, "r", encoding="utf-8") as f:
                data = json.load(f)
                sessions.append({
                    "id": filename[:-5],  # 去掉 .json
                    "title": data.get("title", "未命名会话"),
                    "created": data.get("created", ""),
                    "message_count": len(data.get("messages", []))
                })
    return sorted(sessions, key=lambda x: x["created"], reverse=True)

def save_session(session_id: str, title: str, messages: list):
    """保存会话"""
    ensure_sessions_dir()
    filepath = os.path.join(SESSIONS_DIR, f"{session_id}.json")
    data = {
        "id": session_id,
        "title": title,
        "created": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "messages": messages
    }
    with open(filepath, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def load_session(session_id: str) -> dict:
    """加载会话"""
    filepath = os.path.join(SESSIONS_DIR, f"{session_id}.json")
    with open(filepath, "r", encoding="utf-8") as f:
        return json.load(f)

def delete_session(session_id: str):
    """删除会话"""
    filepath = os.path.join(SESSIONS_DIR, f"{session_id}.json")
    if os.path.exists(filepath):
        os.remove(filepath)

def generate_title(messages: list) -> str:
    """根据对话内容生成标题"""
    if len(messages) <= 1:
        return "新对话"

    # 获取前几条对话
    early_content = "\n".join([
        f"{m['role']}: {m['content'][:100]}"
        for m in messages[:3] if m['role'] != 'system'
    ])

    try:
        response = client.chat.completions.create(
            model=MODEL,
            messages=[{
                "role": "user",
                "content": f"根据以下对话内容，生成一个简短的标题（不超过10个字）：\n{early_content}"
            }],
            temperature=0.5,
            max_tokens=20
        )
        return response.choices[0].message.content.strip().strip('"').strip("'")
    except:
        return "对话 " + datetime.now().strftime("%m-%d %H:%M")

def trim_messages(messages: list, max_length: int = 15) -> list:
    """保持消息列表长度"""
    if len(messages) <= max_length:
        return messages

    system_msgs = [m for m in messages if m["role"] == "system"]
    recent_msgs = messages[-(max_length - len(system_msgs)):]

    return system_msgs + recent_msgs

def chat_loop(session_id: str = None, messages: list = None):
    """聊天循环"""
    if messages is None:
        messages = [{
            "role": "system",
            "content": "你是一个友好的AI助手。"
        }]

    if session_id:
        print(f"\n📂 已加载会话：{session_id}")
    else:
        session_id = datetime.now().strftime("%Y%m%d_%H%M%S")
        print(f"\n📝 新会话：{session_id}")

    print("💡 输入 'save' 保存，'list' 查看所有会话，'load ID' 加载会话，'quit' 退出\n")

    while True:
        user_input = input("你：")

        if user_input.lower() == 'quit':
            # 退出前询问是否保存
            if len(messages) > 1:
                save = input("是否保存当前会话？(y/n): ").lower()
                if save == 'y':
                    title = input("请输入会话标题（直接回车自动生成）：") or None
                    if not title:
                        title = generate_title(messages)
                    save_session(session_id, title, messages)
                    print(f"✅ 已保存：{title}")
            break

        if user_input.lower() == 'save':
            title = input("请输入会话标题：")
            save_session(session_id, title, messages)
            print(f"✅ 已保存\n")
            continue

        if user_input.lower() == 'list':
            print("\n📋 所有会话：")
            sessions = list_sessions()
            for i, s in enumerate(sessions[:10], 1):
                print(f"  {i}. [{s['id']}] {s['title']} ({s['created']}) - {s['message_count']} 条消息")
            print()
            continue

        if user_input.lower().startswith('load '):
            sid = user_input.split(' ', 1)[1].strip()
            try:
                data = load_session(sid)
                return data['id'], data['messages']
            except FileNotFoundError:
                print(f"❌ 会话 {sid} 不存在\n")
                continue

        # 正常对话
        messages.append({"role": "user", "content": user_input})

        response = client.chat.completions.create(
            model=MODEL,
            messages=messages
        )

        ai_reply = response.choices[0].message.content
        messages.append({"role": "assistant", "content": ai_reply})

        print(f"\n助手：{ai_reply}\n")

        # 定期裁剪
        messages = trim_messages(messages)

        # 显示 token（如果支持）
        if response.usage:
            print(f"📊 {response.usage.total_tokens} tokens\n")

    return session_id, messages

def main():
    print("=" * 50)
    print("        💾 长期记忆聊天助手")
    print("=" * 50)

    # 显示已有会话
    sessions = list_sessions()
    if sessions:
        print("\n📋 已保存的会话：")
        for i, s in enumerate(sessions[:5], 1):
            print(f"  {i}. {s['title']} ({s['created']})")

        choice = input("\n输入会话编号继续，或按回车开始新对话：").strip()
        if choice.isdigit() and 1 <= int(choice) <= len(sessions):
            sid = sessions[int(choice) - 1]['id']
            data = load_session(sid)
            chat_loop(data['id'], data['messages'])
            return

    # 开始新对话
    chat_loop()

if __name__ == "__main__":
    main()
```

**运行测试**：

```bash
python lesson1/persistent_chat.py
```

**功能演示**：

```
💾 长期记忆聊天助手
==================================================

📋 已保存的会话：
  1. Python 学习讨论 (2024-03-10 14:30)
  2. 聊聊人工智能 (2024-03-09 20:15)

输入会话编号继续，或按回车开始新对话：1

📂 已加载会话：20240310_143000
💡 输入 'save' 保存，'list' 查看所有会话，'load ID' 加载会话，'quit' 退出

你：我们刚才聊到哪了？
助手：我们刚才在讨论 Python 的装饰器...
```

---

## 六、进阶：记忆架构设计

### 6.1 记忆类型分类

```
┌─────────────────────────────────────────────────────────┐
│                    Agent 记忆系统                        │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ 感知记忆 │  │ 短期记忆 │  │ 长期记忆 │              │
│  │ (缓冲区) │  │ (会话中) │  │ (持久化) │              │
│  └──────────┘  └──────────┘  └──────────┘              │
│       ↓             ↓              ↓                    │
│   当前上下文    最近对话       用户画像/知识库            │
│   1-2 条消息    10-20 轮       向量检索/摘要             │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### 6.2 向量检索记忆（预告）

在第五课《知识检索与 RAG》中，我们将学习：
- 使用 Embedding 将对话转为向量
- 存储到向量数据库
- 语义检索相关记忆

---

## 七、常见问题

### Q1：如何选择合适的记忆策略？

| 场景 | 推荐策略 |
|------|----------|
| 短问答客服 | 保留最近 5-10 轮 |
| 个人助理 | 智能总结 + 关键信息存储 |
| 技术顾问 | 语义检索 + 向量库 |
| 长期陪伴 | 混合策略（所有） |

### Q2：Token 计算不准确怎么办？

不同模型的 token 计算方式不同，建议：
- 使用各模型对应的 tokenizer
- 或预留 20% 的安全余量

### Q3：如何防止 AI"产生虚假记忆"？

- 在 System Prompt 中强调"只确认明确说过的信息"
- 将存储的记忆设为只读，不允许 AI 修改

### Q4：对话历史存储在哪里？

| 存储方式 | 优点 | 缺点 |
|----------|------|------|
| JSON 文件 | 简单易用 | 不适合大量数据 |
| SQLite | 轻量级数据库 | 并发能力有限 |
| 向量数据库 | 支持语义检索 | 需要额外服务 |
| 云存储 | 便于多端同步 | 依赖网络 |

---

## 八、练习题

### 必做（基础巩固）

1. **修改 `memory_bot.py`**：添加"忘记"功能，用户可以命令 AI 忘记某些信息

2. **实现 Token 预警**：当接近 token 限制时，提醒用户并自动总结

### 选做（进阶挑战）

3. **实现语义搜索记忆**：使用第 5 课的知识，让用户可以搜索"我们聊过关于 X 的内容吗？"

4. **创建对话时间线**：按时间展示与 AI 的所有对话历史，类似微信聊天记录

5. **实现多用户支持**：让聊天助手支持多个用户，各自有独立的记忆

---

## 本课小结

✅ 你学会了：
- 对话窗口限制和 Token 计算
- 常见记忆管理策略（保留最近、滑动窗口、智能总结）
- 实现有记忆的聊天助手
- 持久化保存和恢复对话

## 下节课预告

**第三课：你的第一个真正 Agent**
- 理解 Agent 的核心组件：感知 → 规划 → 行动
- 实现能执行动作的 Agent
- 让 AI 决定调用什么函数

---

> 🎉 完成第二课！你的 AI 已经能"记住"用户了！
