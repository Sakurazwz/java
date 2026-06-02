import { useState, useEffect, useCallback } from "react"
import { Container, Row, Col, Card, Badge, Table, Alert } from "react-bootstrap"
import { borrowApi, bookApi, userApi, authApi } from "../../../services/api"
import "./overview.css"

const Overview = () => {
    const [stats, setStats] = useState({ totalBorrowed: 0, totalOverdue: 0, totalBooks: 0 })
    const [overdueList, setOverdueList] = useState([])
    const [bookMap, setBookMap] = useState({})
    const [userMap, setUserMap] = useState({})
    const [error, setError] = useState("")
    const isAdmin = authApi.isAdmin()

    const loadData = useCallback(async () => {
        try {
            const [borrows, books, users] = await Promise.all([
                borrowApi.getAllBorrows(),
                bookApi.getAllBooks(),
                userApi.getAllUsers(),
            ])
            const today = new Date().toISOString().slice(0, 10)
            const overdue = borrows.filter(b => b.returnDate && b.returnDate < today)
            setStats({
                totalBorrowed: borrows.length,
                totalOverdue: overdue.length,
                totalBooks: books.length,
            })
            setOverdueList(overdue)
            const bMap = {}
            books.forEach(b => { bMap[b.id] = b })
            setBookMap(bMap)
            const uMap = {}
            users.forEach(u => { uMap[u.id] = u.name })
            setUserMap(uMap)
        } catch (err) {
            setError("加载概览数据失败: " + err.message)
        }
    }, [])

    useEffect(() => {
        loadData()
    }, [loadData])

    if (!isAdmin) {
        return (
            <Container className="mt-4">
                <Alert variant="danger">无权限：仅管理员可查看馆藏概览</Alert>
            </Container>
        )
    }

    return (
        <Container className="mt-4">
            <div className="page-header">
                <h2>&#128202; 馆藏概览</h2>
            </div>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

            {/* 统计卡片 */}
            <Row className="g-4 mb-4">
                <Col md={4}>
                    <Card className="overview-stat-card overview-total">
                        <Card.Body className="text-center">
                            <div className="overview-stat-icon">&#128218;</div>
                            <div className="overview-stat-num">{stats.totalBooks}</div>
                            <div className="overview-stat-label">馆藏图书</div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card className="overview-stat-card overview-borrowed">
                        <Card.Body className="text-center">
                            <div className="overview-stat-icon">&#128203;</div>
                            <div className="overview-stat-num">{stats.totalBorrowed}</div>
                            <div className="overview-stat-label">借阅中</div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card className="overview-stat-card overview-overdue">
                        <Card.Body className="text-center">
                            <div className="overview-stat-icon">&#9888;&#65039;</div>
                            <div className="overview-stat-num">{stats.totalOverdue}</div>
                            <div className="overview-stat-label">逾期图书</div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 逾期图书列表 */}
            <div className="content-card">
                <h5 className="mb-3" style={{ color: "#e74c3c" }}>&#9888;&#65039; 逾期图书详情</h5>
                {overdueList.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-state-icon">&#127881;</div>
                        <p>暂无逾期图书，一切正常！</p>
                    </div>
                ) : (
                    <Table hover className="mb-0">
                        <thead>
                            <tr>
                                <th>借阅ID</th>
                                <th>书名</th>
                                <th>ISBN</th>
                                <th>借阅人</th>
                                <th>借出日期</th>
                                <th>应还日期</th>
                            </tr>
                        </thead>
                        <tbody>
                            {overdueList.map(record => {
                                const book = bookMap[record.bookId]
                                return (
                                    <tr key={record.id} className="overdue-table-row">
                                        <td><code>{record.id}</code></td>
                                        <td><strong>{book?.title || "未知书名"}</strong></td>
                                        <td><small className="text-muted">{book?.isbn || "-"}</small></td>
                                        <td>{userMap[record.userId] || record.userId}</td>
                                        <td>{record.borrowDate}</td>
                                        <td>
                                            {record.returnDate}
                                            <Badge bg="danger" className="ms-2 overdue-badge">逾期</Badge>
                                        </td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </Table>
                )}
            </div>
        </Container>
    )
}

export default Overview
