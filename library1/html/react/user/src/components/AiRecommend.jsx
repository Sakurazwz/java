import { useState, useRef, useEffect } from "react"
import { Button, Form, Spinner } from "react-bootstrap"
import { useNavigate, useLocation } from "react-router-dom"
import { bookApi, authApi } from "../services/api"
import "./AiRecommend.css"

const AiRecommend = () => {
    const location = useLocation()
    // 未登录或登录/注册页面不显示
    if (!authApi.isAuthenticated() || location.pathname === "/login" || location.pathname === "/register") return null
    const [isOpen, setIsOpen] = useState(false)
    const [messages, setMessages] = useState([])
    const [input, setInput] = useState("")
    const [loading, setLoading] = useState(false)
    const chatEndRef = useRef(null)
    const navigate = useNavigate()

    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: "smooth" })
    }, [messages])

    const handleSend = async () => {
        const query = input.trim()
        if (!query || loading) return

        setInput("")
        setMessages((prev) => [...prev, { role: "user", content: query }])
        setLoading(true)

        try {
            const data = await bookApi.recommend(query)
            setMessages((prev) => [...prev, { role: "ai", content: data.recommendation }])
        } catch (err) {
            setMessages((prev) => [...prev, { role: "ai", content: "抱歉，推荐服务暂时不可用：" + err.message }])
        } finally {
            setLoading(false)
        }
    }

    const handleKeyDown = (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault()
            handleSend()
        }
    }

    const handleBookClick = (title) => {
        // 通过 sessionStorage 传递搜索词，跳转到图书页面后自动搜索
        setIsOpen(false)
        navigate("/books?search=" + encodeURIComponent(title))
    }

    // 将文本中《书名》渲染为可点击链接
    const renderContent = (text) => {
        const parts = text.split(/《|》/)
        return parts.map((part, i) => {
            // 奇数索引 = 书名内容（被《和》包裹的）
            if (i % 2 === 1 && part.trim()) {
                return (
                    <span
                        key={i}
                        className="ai-book-link"
                        onClick={() => handleBookClick(part.trim())}
                    >
                        《{part}》
                    </span>
                )
            }
            return <span key={i}>{part}</span>
        })
    }

    return (
        <>
            <div
                className={`ai-float-btn ${isOpen ? "d-none" : ""}`}
                onClick={() => setIsOpen(true)}
                title="智能图书推荐"
            >
                💻
            </div>

            <div className={`ai-panel ${isOpen ? "open" : ""}`}>
                <div className="ai-panel-header">
                    <span>💻 智能图书推荐</span>
                    <Button
                        variant="link"
                        className="ai-close-btn"
                        onClick={() => setIsOpen(false)}
                    >
                        &times;
                    </Button>
                </div>
                <div className="ai-panel-body">
                    {messages.length === 0 && (
                        <div className="ai-welcome">
                            <div className="ai-welcome-icon">📚</div>
                            <p>告诉我你想看什么书，我帮你推荐！</p>
                            <div className="ai-suggestions">
                                <span onClick={() => setInput("推荐几本热门书")}>热门推荐</span>
                                <span onClick={() => setInput("有没有适合入门的技术书")}>技术入门</span>
                                <span onClick={() => setInput("推荐文学作品")}>文学经典</span>
                            </div>
                        </div>
                    )}
                    {messages.map((msg, i) => (
                        <div key={i} className={`ai-message ${msg.role}`}>
                            <div className="ai-message-avatar">
                                {msg.role === "user" ? "👤" : "💻"}
                            </div>
                            <div className="ai-message-bubble">
                                {msg.role === "ai" ? renderContent(msg.content) : msg.content}
                            </div>
                        </div>
                    ))}
                    {loading && (
                        <div className="ai-message ai">
                            <div className="ai-message-avatar">💻</div>
                            <div className="ai-message-bubble ai-loading">
                                <Spinner animation="border" size="sm" /> 思考中...
                            </div>
                        </div>
                    )}
                    <div ref={chatEndRef} />
                </div>
                <div className="ai-panel-footer">
                    <Form.Control
                        type="text"
                        placeholder="输入你的阅读偏好..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={handleKeyDown}
                        disabled={loading}
                    />
                    <Button
                        variant="primary"
                        size="sm"
                        onClick={handleSend}
                        disabled={loading || !input.trim()}
                    >
                        发送
                    </Button>
                </div>
            </div>
        </>
    )
}

export default AiRecommend
