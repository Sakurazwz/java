import { useState, useEffect, useCallback } from "react"
import { Table, Alert, Container, Badge, Form, Button } from "react-bootstrap"
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
    // 搜索条件
    const [searchBookName, setSearchBookName] = useState("")
    const [searchUserName, setSearchUserName] = useState("")
    const [searchStartDate, setSearchStartDate] = useState("")
    const [searchEndDate, setSearchEndDate] = useState("")

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

    // 加载历史（普通用户始终传自己的 userId）
    const loadHistories = useCallback(async (filters = {}) => {
        try {
            if (isAdmin) {
                const data = await historyApi.getAllHistory(filters)
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
        if (isAdmin) loadUserMap()
    }, [loadBookMap, loadUserMap, isAdmin])

    useEffect(() => {
        if (currentUserId != null) loadHistories()
    }, [currentUserId, loadHistories])

    // 前端过滤：书名、用户名、日期区间全部前端过滤
    const filteredHistories = histories.filter(h => {
        if (searchBookName.trim()) {
            const book = bookMap[h.bookId]
            if (!book || !book.title?.toLowerCase().includes(searchBookName.trim().toLowerCase())) return false
        }
        if (searchUserName.trim() && isAdmin) {
            const name = userMap[h.userId] || String(h.userId)
            if (!String(name).toLowerCase().includes(searchUserName.trim().toLowerCase())) return false
        }
        if (searchStartDate) {
            const recordDate = h.date?.slice(0, 10) // 取日期部分 YYYY-MM-DD
            if (!recordDate || recordDate < searchStartDate) return false
        }
        if (searchEndDate) {
            const recordDate = h.date?.slice(0, 10)
            if (!recordDate || recordDate > searchEndDate) return false
        }
        return true
    })

    const handleSearch = () => {
        // 纯前端过滤，重新加载一次数据即可
        loadHistories()
    }

    const handleReset = () => {
        setSearchBookName("")
        setSearchUserName("")
        setSearchStartDate("")
        setSearchEndDate("")
        loadHistories()
    }

    return (
        <Container className="mt-4">
            <div className="page-header">
                <h2>&#128337; 借阅历史</h2>
                <Badge bg={isAdmin ? "warning" : "secondary"} className="role-badge">
                    {isAdmin ? "管理员 - 全部记录" : "我的记录"}
                </Badge>
            </div>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

            <div className="content-card mb-4">
                <div className="d-flex gap-3 align-items-end search-box" style={{ flexWrap: "wrap" }}>
                    <Form.Group className="mb-0" style={{ minWidth: "160px" }}>
                        <Form.Label className="small text-muted">书名</Form.Label>
                        <Form.Control
                            size="sm"
                            placeholder="按书名筛选..."
                            value={searchBookName}
                            onChange={(e) => setSearchBookName(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                        />
                    </Form.Group>
                    {isAdmin && (
                        <Form.Group className="mb-0" style={{ minWidth: "160px" }}>
                            <Form.Label className="small text-muted">用户名称</Form.Label>
                            <Form.Control
                                size="sm"
                                placeholder="按用户筛选..."
                                value={searchUserName}
                                onChange={(e) => setSearchUserName(e.target.value)}
                                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                            />
                        </Form.Group>
                    )}
                    <Form.Group className="mb-0" style={{ minWidth: "160px" }}>
                        <Form.Label className="small text-muted">开始日期</Form.Label>
                        <Form.Control
                            size="sm"
                            type="date"
                            value={searchStartDate}
                            max={searchEndDate || undefined}
                            onChange={(e) => {
                                const val = e.target.value
                                setSearchStartDate(val)
                                // 如果结束日期已选且早于开始日期，自动清空结束日期
                                if (searchEndDate && val > searchEndDate) {
                                    setSearchEndDate("")
                                }
                            }}
                        />
                    </Form.Group>
                    <Form.Group className="mb-0" style={{ minWidth: "160px" }}>
                        <Form.Label className="small text-muted">结束日期</Form.Label>
                        <Form.Control
                            size="sm"
                            type="date"
                            value={searchEndDate}
                            min={searchStartDate || undefined}
                            onChange={(e) => setSearchEndDate(e.target.value)}
                        />
                    </Form.Group>
                    <Button variant="primary" size="sm" onClick={handleSearch}>&#128269; 搜索</Button>
                    <Button variant="outline-secondary" size="sm" onClick={handleReset}>&#128472; 重置</Button>
                </div>
            </div>

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
                        {filteredHistories.map((h) => {
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

            {filteredHistories.length === 0 && !error && (
                <div className="content-card empty-state">
                    <div className="empty-state-icon">&#128220;</div>
                    <p>暂无借阅历史记录</p>
                </div>
            )}
        </Container>
    )
}

export default History
