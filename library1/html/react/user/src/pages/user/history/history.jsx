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
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>借阅历史</h2>
                <Badge bg={isAdmin ? "danger" : "secondary"}>
                    {isAdmin ? "管理员 - 全部记录" : "我的记录"}
                </Badge>
            </div>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>ID</th>
                        {isAdmin && <th>用户</th>}
                        <th>书名</th>
                        <th>ISBN</th>
                        <th>操作</th>
                        <th>日期</th>
                    </tr>
                </thead>
                <tbody>
                    {histories.map((h) => {
                        const book = bookMap[h.bookId]
                        return (
                            <tr key={h.id}>
                                <td>{h.id}</td>
                                {isAdmin && <td>{userMap[h.userId] || h.userId}</td>}
                                <td>{book?.title || "-"}</td>
                                <td>{book?.isbn || "-"}</td>
                                <td>{h.behaviour}</td>
                                <td>{h.date}</td>
                            </tr>
                        )
                    })}
                </tbody>
            </Table>

            {histories.length === 0 && !error && (
                <Alert variant="info">暂无借阅历史记录</Alert>
            )}
        </Container>
    )
}

export default History
