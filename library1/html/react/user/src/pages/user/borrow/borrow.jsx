import { useState, useEffect } from "react"
import { Table, Button, Form, Modal, Alert, Container } from "react-bootstrap"
import { borrowApi, bookApi, authApi } from "../../../services/api"
import "./borrow.css"

const Borrow = () => {
    const [borrows, setBorrows] = useState([])
    const [bookMap, setBookMap] = useState({}) // bookId -> book对象
    const [error, setError] = useState("")
    const [success, setSuccess] = useState("")
    const [isAdmin, setIsAdmin] = useState(false)
    const [currentUserId, setCurrentUserId] = useState(null)

    // 借书表单
    const [showBorrowModal, setShowBorrowModal] = useState(false)
    const [borrowForm, setBorrowForm] = useState({
        bookId: "",
        userId: "",
    })

    // 还书表单
    const [showReturnModal, setShowReturnModal] = useState(false)
    const [returnForm, setReturnForm] = useState({
        bookId: "",
        userId: "",
    })

    useEffect(() => {
        const admin = authApi.isAdmin()
        setIsAdmin(admin)
        const user = authApi.getCurrentUser()
        if (user) {
            setCurrentUserId(user.id)
        }
        loadBookMap()
    }, [])

    // currentUserId 就绪后再加载借阅记录
    useEffect(() => {
        if (currentUserId != null) {
            loadBorrows()
        }
    }, [currentUserId, isAdmin])

    const loadBookMap = async () => {
        try {
            const books = await bookApi.getAllBooks()
            const map = {}
            books.forEach((b) => { map[b.id] = b })
            setBookMap(map)
        } catch (err) {
            console.error("加载图书信息失败:", err)
        }
    }

    const loadBorrows = async () => {
        try {
            // 普通用户只查自己的，管理员查全部
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
    }

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
            <h2>借阅管理</h2>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}
            {success && <Alert variant="success" dismissible onClose={() => setSuccess("")}>{success}</Alert>}

            <div className="d-flex justify-content-between mb-3">
                <Button variant="outline-secondary" onClick={loadBorrows}>
                    刷新
                </Button>
                <div>
                    <Button variant="primary" className="me-2" onClick={() => setShowBorrowModal(true)}>
                        +借书
                    </Button>
                    <Button variant="warning" onClick={() => setShowReturnModal(true)}>
                        还书
                    </Button>
                </div>
            </div>

            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>借阅ID</th>
                        <th>图书ID</th>
                        <th>书名</th>
                        <th>ISBN</th>
                        <th>用户ID</th>
                        <th>借出日期</th>
                        <th>应还日期</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    {borrows.map((borrow) => {
                        const book = bookMap[borrow.bookId]
                        return (
                        <tr key={borrow.id}>
                            <td>{borrow.id}</td>
                            <td>{borrow.bookId}</td>
                            <td>{book?.title || "-"}</td>
                            <td>{book?.isbn || "-"}</td>
                            <td>{borrow.userId}</td>
                            <td>{borrow.borrowDate}</td>
                            <td>{borrow.returnDate}</td>
                            <td>
                                <Button
                                    variant="success"
                                    size="sm"
                                    className="me-2"
                                    onClick={() => handleRenew(borrow.bookId, borrow.userId)}
                                >
                                    续借
                                </Button>
                                <Button
                                    variant="warning"
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

            {borrows.length === 0 && !error && (
                <Alert variant="info">暂无借阅记录</Alert>
            )}

            {/* 借书模态框 */}
            <Modal show={showBorrowModal} onHide={() => setShowBorrowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>借书</Modal.Title>
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
                                <Form.Text className="text-muted">
                                    普通用户只能为自己借书
                                </Form.Text>
                            )}
                        </Form.Group>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowBorrowModal(false)}>
                            取消
                        </Button>
                        <Button variant="primary" type="submit">
                            确认借书
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>

            {/* 还书模态框 */}
            <Modal show={showReturnModal} onHide={() => setShowReturnModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>还书</Modal.Title>
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
                        <Button variant="secondary" onClick={() => setShowReturnModal(false)}>
                            取消
                        </Button>
                        <Button variant="warning" type="submit">
                            确认还书
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </Container>
    )
}

export default Borrow
