import { useState, useEffect, useCallback } from "react"
import { Container, Row, Col, Card, Badge, Alert } from "react-bootstrap"
import { authApi, borrowApi, bookApi } from "../../../services/api"
import "./profile.css"

const UserProfile = () => {
    const [currentUser] = useState(() => {
        const user = authApi.getCurrentUser()
        return user ? { id: user.id, name: user.name, role: user.role } : null
    })
    const [borrowRecords, setBorrowRecords] = useState([])
    const [overdueRecords, setOverdueRecords] = useState([])
    const [bookMap, setBookMap] = useState({})
    const [error, setError] = useState("")
    const isAdmin = authApi.isAdmin()

    const loadData = useCallback(async () => {
        if (!currentUser) return
        try {
            const [borrows, overdue, books] = await Promise.all([
                borrowApi.getUserBorrows(currentUser.id),
                borrowApi.getOverdueBorrows(currentUser.id),
                bookApi.getAllBooks(),
            ])
            setBorrowRecords(borrows)
            setOverdueRecords(overdue)
            const map = {}
            books.forEach(b => { map[b.id] = b })
            setBookMap(map)
        } catch (err) {
            setError("加载数据失败: " + err.message)
        }
    }, [currentUser])

    useEffect(() => {
        loadData()
    }, [loadData])

    if (!currentUser) return null

    const totalBorrowed = borrowRecords.length
    const totalOverdue = overdueRecords.length

    return (
        <Container className="mt-4">
            <div className="page-header">
                <h2>&#128100; 用户中心</h2>
                <Badge bg={isAdmin ? "warning" : "primary"} className="role-badge">
                    {isAdmin ? "管理员" : "普通用户"}
                </Badge>
            </div>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

            {/* 用户信息卡片 */}
            <Row className="g-4 mb-4">
                <Col md={4}>
                    <Card className="profile-card user-info-card">
                        <Card.Body className="text-center">
                            <div className="profile-avatar">
                                {currentUser.name?.charAt(0)?.toUpperCase() || "?"}
                            </div>
                            <h4 className="mt-3 mb-1">{currentUser.name}</h4>
                            <Badge bg={isAdmin ? "warning" : "primary"} className="mb-3">
                                {isAdmin ? "管理员" : "普通用户"}
                            </Badge>
                            <div className="user-id">ID: {currentUser.id}</div>
                        </Card.Body>
                    </Card>
                </Col>

                {/* 统计卡片 */}
                <Col md={8}>
                    <Row className="g-4">
                        <Col sm={4}>
                            <Card className="profile-card stat-card stat-total">
                                <Card.Body className="text-center">
                                    <div className="stat-icon">&#128218;</div>
                                    <div className="stat-number">{totalBorrowed}</div>
                                    <div className="stat-label">已借图书</div>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col sm={4}>
                            <Card className="profile-card stat-card stat-active">
                                <Card.Body className="text-center">
                                    <div className="stat-icon">&#128203;</div>
                                    <div className="stat-number">{totalBorrowed - totalOverdue}</div>
                                    <div className="stat-label">未还图书</div>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col sm={4}>
                            <Card className="profile-card stat-card stat-overdue">
                                <Card.Body className="text-center">
                                    <div className="stat-icon">&#9888;&#65039;</div>
                                    <div className="stat-number">{totalOverdue}</div>
                                    <div className="stat-label">逾期图书</div>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </Col>
            </Row>

            {/* 当前借阅列表 */}
            <div className="content-card mb-4">
                <h5 className="mb-3">&#128203; 当前借阅</h5>
                {borrowRecords.length === 0 ? (
                    <p className="text-muted text-center py-3 mb-0">暂无借阅记录</p>
                ) : (
                    <Row xs={1} sm={2} md={3} lg={4} className="g-3">
                        {borrowRecords.map(record => {
                            const book = bookMap[record.bookId]
                            const today = new Date().toISOString().slice(0, 10)
                            const isOverdue = record.returnDate && record.returnDate < today
                            return (
                                <Col key={record.id}>
                                    <Card className={`profile-borrow-card ${isOverdue ? "profile-overdue" : ""}`}>
                                        <div className="profile-borrow-cover">
                                            <img
                                                src={book?.cover || "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjgwIiB2aWV3Qm94PSIwIDAgMjAwIDI4MCI+PHJlY3Qgd2lkdGg9IjIwMCIgaGVpZ2h0PSIyODAiIGZpbGw9IiNlOWVjZWYiLz48dGV4dCB4PSIxMDAiIHk9IjE0MCIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE2IiBmaWxsPSIjNmM3NTdkIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Yqg6L295ZCO5a2mPC90ZXh0Pjwvc3ZnPg=="}
                                                alt={book?.title || ""}
                                                onError={(e) => { e.target.src = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjgwIiB2aWV3Qm94PSIwIDAgMjAwIDI4MCI+PHJlY3Qgd2lkdGg9IjIwMCIgaGVpZ2h0PSIyODAiIGZpbGw9IiNlOWVjZWYiLz48dGV4dCB4PSIxMDAiIHk9IjE0MCIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE2IiBmaWxsPSIjNmM3NTdkIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Yqg6L295ZCO5a2mPC90ZXh0Pjwvc3ZnPg==" }}
                                            />
                                            {isOverdue && <div className="profile-overdue-overlay">逾期</div>}
                                        </div>
                                        <Card.Body className="p-2">
                                            <div className="profile-book-title">{book?.title || "未知书名"}</div>
                                            <div className="profile-dates">
                                                <span>借出: {record.borrowDate}</span>
                                                <span>应还: {record.returnDate}</span>
                                            </div>
                                        </Card.Body>
                                    </Card>
                                </Col>
                            )
                        })}
                    </Row>
                )}
            </div>
        </Container>
    )
}

export default UserProfile
