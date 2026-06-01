import { useState, useEffect, useCallback } from "react"
import { Table, Button, Form, Modal, Alert, Container } from "react-bootstrap"
import { borrowApi, bookApi, userApi, authApi } from "../../../services/api"
import "./borrow.css"

const Borrow = () => {
    const [borrows, setBorrows] = useState([])
    const [bookMap, setBookMap] = useState({})
    const [userMap, setUserMap] = useState({})
    const [error, setError] = useState("")
    const [success, setSuccess] = useState("")
    const [isAdmin, setIsAdmin] = useState(authApi.isAdmin())
    const [currentUserId, setCurrentUserId] = useState(() => {
        const user = authApi.getCurrentUser()
        return user ? user.id : null
    })

    const [showBorrowModal, setShowBorrowModal] = useState(false)
    const [borrowForm, setBorrowForm] = useState({ bookId: "", userId: "" })

    const [showReturnModal, setShowReturnModal] = useState(false)
    const [returnForm, setReturnForm] = useState({ bookId: "", userId: "" })

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

    const loadBorrows = useCallback(async () => {
        try {
            if (isAdmin) {
                const data = await borrowApi.getAllBorrows()
                setBorrows(data)
            } else if (currentUserId) {
                const data = await borrowApi.getUserBorrows(currentUserId)
                setBorrows(data)
            }
        } catch (err) {
            setError("加载借阅记录失败: " + err.message)
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
            loadBorrows()
        }
    }, [currentUserId, isAdmin, loadBorrows])

    const handleBorrow = async (e) => {
        e.preventDefault()
        setError("")
        setSuccess("")
        const userId = isAdmin ? borrowForm.userId : currentUserId
        try {
            await borrowApi.borrowBook(borrowForm.bookId, userId)
            setSuccess("借书成功！")
            setShowBorrowModal(false)
            loadBorrows()
        } catch (err) {
            setError("借书失败: " + err.message)
        }
    }

    const handleReturn = async (e) => {
        e.preventDefault()
        setError("")
        setSuccess("")
        try {
            await borrowApi.returnBook(returnForm.bookId, isAdmin ? (returnForm.userId || currentUserId) : currentUserId)
            setSuccess("还书成功！")
            setShowReturnModal(false)
            loadBorrows()
        } catch (err) {
            setError("还书失败: " + err.message)
        }
    }

    const handleRenew = async (bookId, userId) => {
        if (!window.confirm("确定要续借吗？")) return
        setError("")
        setSuccess("")
        try {
            await borrowApi.renewBook(bookId, userId)
            setSuccess("续借成功！")
            loadBorrows()
        } catch (err) {
            setError("续借失败: " + err.message)
        }
    }

    return (
        <Container className="mt-4">
            <div className="page-header">
                <h2>&#128220; 借阅管理</h2>
                <div className="d-flex gap-2">
                    <Button variant="outline-secondary" size="sm" onClick={loadBorrows}>
                        &#128472; 刷新
                    </Button>
                    <Button variant="success" onClick={() => setShowBorrowModal(true)}>
                        + 借书
                    </Button>
                    <Button variant="warning" onClick={() => setShowReturnModal(true)}>
                        还书
                    </Button>
                </div>
            </div>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}
            {success && <Alert variant="success" dismissible onClose={() => setSuccess("")}>{success}</Alert>}

            <div className="content-card" style={{ padding: 0 }}>
                <Table hover className="mb-0">
                    <thead>
                        <tr>
                            <th>借阅ID</th>
                            <th>图书ID</th>
                            <th>书名</th>
                            <th>ISBN</th>
                            <th>用户</th>
                            <th>借出日期</th>
                            <th>应还日期</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        {borrows.map((borrow) => {
                            const book = bookMap[borrow.bookId]
                            const today = new Date().toISOString().slice(0, 10)
                            const isOverdue = borrow.returnDate && borrow.returnDate < today
                            return (
                                <tr key={borrow.id} className={isOverdue ? "overdue-row" : ""}>
                                    <td><code>{borrow.id}</code></td>
                                    <td>{borrow.bookId}</td>
                                    <td><strong>{book?.title || "-"}</strong></td>
                                    <td><small className="text-muted">{book?.isbn || "-"}</small></td>
                                    <td>{userMap[borrow.userId] || borrow.userId}</td>
                                    <td>{borrow.borrowDate}</td>
                                    <td>
                                        {borrow.returnDate}
                                        {isOverdue && (
                                            <Badge bg="danger" className="ms-2 overdue-badge">逾期</Badge>
                                        )}
                                    </td>
                                    <td>
                                        <Button
                                            variant="warning"
                                            size="sm"
                                            className="me-2"
                                            onClick={() => handleRenew(borrow.bookId, borrow.userId)}
                                        >
                                            续借
                                        </Button>
                                        <Button
                                            variant="danger"
                                            size="sm"
                                            onClick={() => {
                                                setReturnForm({ bookId: borrow.bookId, userId: borrow.userId || "" })
                                                setShowReturnModal(true)
                                            }}
                                        >
                                            还书
                                        </Button>
                                    </td>
                                </tr>
                            )
                        })}
                    </tbody>
                </Table>
            </div>

            {borrows.length === 0 && !error && (
                <div className="content-card empty-state">
                    <div className="empty-state-icon">&#128230;</div>
                    <p>暂无借阅记录</p>
                </div>
            )}

            <Modal show={showBorrowModal} onHide={() => setShowBorrowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>&#128214; 借书</Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleBorrow}>
                    <Modal.Body>
                        <Form.Group className="mb-3">
                            <Form.Label>图书ID</Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="请输入图书ID"
                                value={borrowForm.bookId}
                                onChange={(e) => setBorrowForm({ ...borrowForm, bookId: e.target.value })}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>用户ID</Form.Label>
                            <Form.Control
                                type="number"
                                placeholder={isAdmin ? "请输入用户ID" : "当前用户"}
                                value={isAdmin ? borrowForm.userId : (currentUserId || "")}
                                onChange={(e) => setBorrowForm({ ...borrowForm, userId: e.target.value })}
                                disabled={!isAdmin}
                                required
                            />
                            {!isAdmin && (
                                <Form.Text className="text-muted">普通用户只能为自己借书</Form.Text>
                            )}
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowBorrowModal(false)}>取消</Button>
                        <Button variant="success" type="submit">确认借书</Button>
                    </Modal.Footer>
                </Form>
            </Modal>

            <Modal show={showReturnModal} onHide={() => setShowReturnModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>&#128230; 还书</Modal.Title>
                </Modal.Header>
                <Form onSubmit={handleReturn}>
                    <Modal.Body>
                        <Form.Group className="mb-3">
                            <Form.Label>图书ID</Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="请输入要归还的图书ID"
                                value={returnForm.bookId}
                                onChange={(e) => setReturnForm({ ...returnForm, bookId: e.target.value })}
                                required
                            />
                        </Form.Group>
                        {isAdmin && (
                            <Form.Group className="mb-3">
                                <Form.Label>用户ID</Form.Label>
                                <Form.Control
                                    type="number"
                                    placeholder="请输入用户ID"
                                    value={returnForm.userId || ""}
                                    onChange={(e) => setReturnForm({ ...returnForm, userId: e.target.value })}
                                />
                            </Form.Group>
                        )}
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowReturnModal(false)}>取消</Button>
                        <Button variant="warning" type="submit">确认还书</Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Container>
    )
}

export default Borrow
