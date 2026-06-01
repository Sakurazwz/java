import { useState, useEffect, useCallback } from "react"
import { Table, Alert, Container, Badge } from "react-bootstrap"
import { historyApi, bookApi, userApi, authApi } from "../../../services/api"
import "./history.css"

const History = () => {
    const [histories, setHistories] = useState([])
    const [bookMap, setBookMap] = useState({})
    const [userMap, setUserMap] = useState({})
    const [error, setError] = useState("")
    const [isAdmin] = useState(authApi.isAdmin())
    const [currentUserId] = useState(() => {
        const user = authApi.getCurrentUser()
        return user ? user.id : null
    })

    const loadBookMap = useCallback(async () => {
        try {
            const books = await bookApi.getAllBooks()
            const map = {}
            books.forEach((b) => { map[b.id] = b })
            setBookMap(map)
        } catch (err) {
            console.error("加载图书信息失败:", err)
        }
    }, [])

    const loadUserMap = useCallback(async () => {
        try {
            const users = await userApi.getAllUsers()
            const map = {}
            users.forEach((u) => { map[u.id] = u.name })
            setUserMap(map)
        } catch (err) {
            console.error("加载用户信息失败:", err)
        }
    }, [])

    const loadHistories = useCallback(async () => {
        try {
            if (isAdmin) {
                const data = await historyApi.getAllHistory()
                setHistories(data)
            } else if (currentUserId) {
                const data = await historyApi.getHistoryByUserId(currentUserId)
                setHistories(data)
            }
        } catch (err) {
            setError("加载借阅历史失败: " + err.message)
        }
    }, [isAdmin, currentUserId])

    useEffect(() => {
        loadBookMap()
        if (isAdmin) {
            loadUserMap()
        }
    }, [loadBookMap, loadUserMap, isAdmin])

    useEffect(() => {
        if (currentUserId != null) {
            loadHistories()
        }
    }, [currentUserId, loadHistories])

    return (
        <Container className="mt-4">
            <div className="page-header">
                <h2>&#128337; 借阅历史</h2>
                <Badge bg={isAdmin ? "warning" : "secondary"} className="role-badge">
                    {isAdmin ? "管理员 - 全部记录" : "我的记录"}
                </Badge>
            </div>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

            <div className="content-card" style={{ padding: 0 }}>
                <Table hover className="mb-0">
                    <thead>
                        <tr>
                            <th style={{ width: "8%" }}>ID</th>
                            {isAdmin && <th style={{ width: "14%" }}>用户</th>}
                            <th style={{ width: "20%" }}>书名</th>
                            <th style={{ width: "14%" }}>ISBN</th>
                            <th style={{ width: "12%" }}>操作</th>
                            <th style={{ width: isAdmin ? "32%" : "46%" }}>日期</th>
                        </tr>
                    </thead>
                    <tbody>
                        {histories.map((h) => {
                            const book = bookMap[h.bookId]
                            return (
                                <tr key={h.id}>
                                    <td><code>{h.id}</code></td>
                                    {isAdmin && <td><strong>{userMap[h.userId] || h.userId}</strong></td>}
                                    <td>{book?.title || <span className="text-muted">-</span>}</td>
                                    <td><small className="text-muted">{book?.isbn || "-"}</small></td>
                                    <td>
                                        <Badge bg={
                                            h.behaviour?.includes("借书") ? "success" :
                                            h.behaviour?.includes("续借") ? "warning" :
                                            h.behaviour?.includes("还书") ? "danger" : "secondary"
                                        }>
                                            {h.behaviour}
                                        </Badge>
                                    </td>
                                    <td><small>{h.date}</small></td>
                                </tr>
                            )
                        })}
                    </tbody>
                </Table>
            </div>

            {histories.length === 0 && !error && (
                <div className="content-card empty-state">
                    <div className="empty-state-icon">&#128220;</div>
                    <p>暂无借阅历史记录</p>
                </div>
            )}
        </Container>
    )
}

export default History
