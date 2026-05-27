import { useState, useEffect } from "react"
import { Table, Button, Form, Alert, Container, Badge, Modal, Card, Row, Col } from "react-bootstrap"
import { bookApi, authApi, borrowApi } from "../../../services/api"
import "./books.css"

// 默认封面占位图（Base64 SVG）
const DEFAULT_COVER = "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjgwIiB2aWV3Qm94PSIwIDAgMjAwIDI4MCI+PHJlY3Qgd2lkdGg9IjIwMCIgaGVpZ2h0PSIyODAiIGZpbGw9IiNlOWVjZWYiLz48dGV4dCB4PSIxMDAiIHk9IjE0MCIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE2IiBmaWxsPSIjNmM3NTdkIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Yqg6L295ZCO5a2mPC90ZXh0Pjwvc3ZnPg=="

const Books = () => {
    const [books, setBooks] = useState([])
    const [searchTerm, setSearchTerm] = useState("")
    const [error, setError] = useState("")
    const [showModal, setShowModal] = useState(false)
    const [editingBook, setEditingBook] = useState(null)
    const [formData, setFormData] = useState({ title: "", author: "", description: "", cover: "", isbn: "", count: 1 })
    const [isAdmin, setIsAdmin] = useState(false)
    const [viewMode, setViewMode] = useState("card")
    const [currentUser, setCurrentUser] = useState(null)
    const [borrowingBookId, setBorrowingBookId] = useState(null)
    const [borrowRecords, setBorrowRecords] = useState([]) // 当前用户的借阅记录

    useEffect(() => {
        loadBooks()
        setIsAdmin(authApi.isAdmin())
        const user = authApi.getCurrentUser()
        if (user) {
            setCurrentUser({ id: user.id, name: user.name })
        }
    }, [])

    // currentUser 就绪后加载借阅记录
    useEffect(() => {
        if (currentUser) {
            loadUserBorrows()
        }
    }, [currentUser])

    const loadBooks = async () => {
        try {
            const data = await bookApi.getAllBooks()
            setBooks(data)
        } catch (err) {
            setError("加载图书列表失败: " + err.message)
        }
    }

    // 加载当前用户的借阅记录
    const loadUserBorrows = async () => {
        try {
            const data = await borrowApi.getUserBorrows(currentUser.id)
            setBorrowRecords(data)
        } catch (err) {
            console.error("获取借阅记录失败:", err)
        }
    }

    // 查询某本书是否被当前用户借阅
    const getMyBorrowRecord = (bookId) => {
        return borrowRecords.find((r) => r.bookId === bookId)
    }

    const handleSearch = async () => {
        if (!searchTerm.trim()) {
            loadBooks()
            return
        }
        try {
            const data = await bookApi.searchBooks(searchTerm)
            setBooks(data)
            setError("")
        } catch (err) {
            setError("搜索失败: " + err.message)
            setBooks([])
        }
    }

    const handleAddClick = () => {
        setEditingBook(null)
        setFormData({ title: "", author: "", description: "", cover: "", isbn: "", count: 1 })
        setShowModal(true)
    }

    const handleEditClick = (book) => {
        setEditingBook(book)
        setFormData({
            title: book.title,
            author: book.author,
            description: book.description || "",
            cover: book.cover || "",
            isbn: book.isbn || "",
            count: book.count ?? 1,
        })
        setShowModal(true)
    }

    const handleSave = async () => {
        try {
            if (editingBook) {
                await bookApi.updateBook({ ...editingBook, ...formData })
            } else {
                await bookApi.addBook(formData)
            }
            setShowModal(false)
            loadBooks()
            setError("")
        } catch (err) {
            setError(editingBook ? "更新图书失败: " + err.message : "添加图书失败: " + err.message)
        }
    }

    const handleDelete = async (id) => {
        if (!window.confirm("确定要删除这本图书吗？")) return
        try {
            await bookApi.deleteBook(id)
            loadBooks()
            setError("")
        } catch (err) {
            setError("删除图书失败: " + err.message)
        }
    }

    // 处理借书
    const handleBorrow = async (bookId) => {
        if (!currentUser) {
            setError("请先登录")
            return
        }
        setBorrowingBookId(bookId)
        try {
            await borrowApi.borrowBook(bookId, currentUser.id)
            setError("")
            alert("借书成功！")
            loadUserBorrows()
        } catch (err) {
            setError("借书失败: " + err.message)
        } finally {
            setBorrowingBookId(null)
        }
    }

    // 处理还书
    const handleReturn = async (bookId) => {
        if (!window.confirm("确定要归还这本书吗？")) return
        try {
            await borrowApi.returnBook(bookId, currentUser.id)
            setError("")
            alert("还书成功！")
            loadUserBorrows()
        } catch (err) {
            setError("还书失败: " + err.message)
        }
    }

    // 处理续借
    const handleRenew = async (bookId) => {
        if (!currentUser) return
        try {
            await borrowApi.renewBook(bookId, currentUser.id)
            setError("")
            alert("续借成功！延长90天")
            loadUserBorrows()
        } catch (err) {
            setError("续借失败: " + err.message)
        }
    }

    const handleFormChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value })
    }

    // 图片文件选择 → 转为 Base64 存入 formData.cover
    const handleCoverUpload = (e) => {
        const file = e.target.files[0]
        if (!file) return

        // 限制文件大小（最大 5MB）
        if (file.size > 5 * 1024 * 1024) {
            setError("封面图片大小不能超过 5MB")
            return
        }

        // 限制文件类型
        if (!file.type.startsWith("image/")) {
            setError("请选择图片文件")
            return
        }

        const reader = new FileReader()
        reader.onload = (event) => {
            setFormData({ ...formData, cover: event.target.result })
        }
        reader.readAsDataURL(file)
    }

    // 获取封面显示地址
    const getCoverSrc = (cover) => {
        return cover || DEFAULT_COVER
    }

    const handleImgError = (e) => {
        e.target.src = DEFAULT_COVER
    }

    return (
        <Container className="mt-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>图书列表</h2>
                <div className="d-flex gap-2">
                    <Button
                        variant={viewMode === "card" ? "primary" : "outline-primary"}
                        size="sm"
                        onClick={() => setViewMode("card")}
                    >
                        卡片视图
                    </Button>
                    <Button
                        variant={viewMode === "list" ? "primary" : "outline-primary"}
                        size="sm"
                        onClick={() => setViewMode("list")}
                    >
                        列表视图
                    </Button>
                </div>
            </div>

            {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

            <div className="d-flex justify-content-between mb-3">
                <div className="d-flex gap-2">
                    <Form.Control
                        type="text"
                        placeholder="搜索图书标题..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ width: "300px" }}
                    />
                    <Button variant="outline-primary" onClick={handleSearch}>
                        搜索
                    </Button>
                    <Button variant="outline-secondary" onClick={loadBooks}>
                        刷新
                    </Button>
                </div>
                <div className="d-flex gap-2">
                    {isAdmin && (
                        <Button variant="primary" onClick={handleAddClick}>
                            + 添加图书
                        </Button>
                    )}
                    <Badge bg={isAdmin ? "danger" : "secondary"}>
                        {isAdmin ? "管理员" : "普通用户 - 只读模式"}
                    </Badge>
                </div>
            </div>

            {/* 卡片视图 */}
            {viewMode === "card" && (
                <Row xs={1} sm={2} md={3} lg={4} className="g-4">
                    {books.map((book) => (
                        <Col key={book.id}>
                            <Card className="book-card h-100">
                                <div className="book-cover-container">
                                    <img
                                        src={getCoverSrc(book.cover)}
                                        onError={handleImgError}
                                        className="book-cover"
                                        alt={book.title}
                                    />
                                </div>
                                <Card.Body className="d-flex flex-column">
                                    <Card.Title className="book-title">{book.title}</Card.Title>
                                    <Card.Subtitle className="mb-2 text-muted">{book.author}</Card.Subtitle>
                                    {book.isbn && (
                                        <div className="mb-1">
                                            <small className="text-muted">ISBN: {book.isbn}</small>
                                        </div>
                                    )}
                                    <div className="mb-1">
                                        <small className="text-muted">
                                            库存: {book.count ?? 0} 册 | 已借: {book.borrowCount ?? 0} 册
                                        </small>
                                    </div>
                                    <Card.Text className="book-description flex-grow-1">
                                        {book.description?.substring(0, 80) || "暂无简介"}
                                        {book.description?.length > 80 ? "..." : ""}
                                    </Card.Text>
                                    {/* 操作按钮区域 */}
                                    <div className="mt-auto d-flex gap-2 flex-wrap">
                                        {getMyBorrowRecord(book.id) ? (
                                            <>
                                                <Button
                                                    variant="warning"
                                                    size="sm"
                                                    onClick={() => handleRenew(book.id)}
                                                >
                                                    续借
                                                </Button>
                                                <Button
                                                    variant="danger"
                                                    size="sm"
                                                    onClick={() => handleReturn(book.id)}
                                                >
                                                    还书
                                                </Button>
                                            </>
                                        ) : (
                                            <Button
                                                variant="success"
                                                size="sm"
                                                disabled={borrowingBookId === book.id || (book.count ?? 0) <= 0}
                                                onClick={() => handleBorrow(book.id)}
                                            >
                                                {borrowingBookId === book.id ? "借书中..." : (book.count ?? 0) <= 0 ? "已借完" : "借书"}
                                            </Button>
                                        )}
                                        {isAdmin && (
                                            <>
                                                <Button
                                                    variant="outline-warning"
                                                    size="sm"
                                                    onClick={() => handleEditClick(book)}
                                                >
                                                    编辑
                                                </Button>
                                                <Button
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => handleDelete(book.id)}
                                                >
                                                    删除
                                                </Button>
                                            </>
                                        )}
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}

            {/* 列表视图 */}
            {viewMode === "list" && (
                <Table striped bordered hover>
                    <thead>
                        <tr>
                            <th style={{ width: "80px" }}>封面</th>
                            <th>标题</th>
                            <th>作者</th>
                            <th>ISBN</th>
                            <th>库存</th>
                            <th>已借</th>
                            <th>简介</th>
                            <th style={{ width: "150px" }}>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        {books.map((book) => (
                            <tr key={book.id}>
                                <td>
                                    <img
                                        src={getCoverSrc(book.cover)}
                                        onError={handleImgError}
                                        alt={book.title}
                                        style={{ width: "60px", height: "80px", objectFit: "cover", borderRadius: "4px" }}
                                    />
                                </td>
                                <td>{book.title}</td>
                                <td>{book.author}</td>
                                <td>{book.isbn || "-"}</td>
                                <td>{book.count ?? 0}</td>
                                <td>{book.borrowCount ?? 0}</td>
                                <td>{book.description?.substring(0, 50) || "暂无简介"}...</td>
                                <td>
                                    <div className="d-flex gap-1">
                                        {getMyBorrowRecord(book.id) ? (
                                            <>
                                                <Button
                                                    variant="warning"
                                                    size="sm"
                                                    onClick={() => handleRenew(book.id)}
                                                >
                                                    续借
                                                </Button>
                                                <Button
                                                    variant="danger"
                                                    size="sm"
                                                    onClick={() => handleReturn(book.id)}
                                                >
                                                    还书
                                                </Button>
                                            </>
                                        ) : (
                                            <Button
                                                variant="success"
                                                size="sm"
                                                disabled={borrowingBookId === book.id || (book.count ?? 0) <= 0}
                                                onClick={() => handleBorrow(book.id)}
                                            >
                                                {borrowingBookId === book.id ? "借书中..." : (book.count ?? 0) <= 0 ? "已借完" : "借书"}
                                            </Button>
                                        )}
                                        {isAdmin && (
                                            <>
                                                <Button
                                                    variant="outline-warning"
                                                    size="sm"
                                                    onClick={() => handleEditClick(book)}
                                                >
                                                    编辑
                                                </Button>
                                                <Button
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => handleDelete(book.id)}
                                                >
                                                    删除
                                                </Button>
                                            </>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            )}

            {books.length === 0 && !error && (
                <Alert variant="info">暂无图书数据</Alert>
            )}

            {/* 添加/编辑图书弹窗 - 仅管理员可见 */}
            {isAdmin && (
                <Modal show={showModal} onHide={() => setShowModal(false)} size="lg">
                    <Modal.Header closeButton>
                        <Modal.Title>{editingBook ? "编辑图书" : "添加图书"}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form>
                            <Form.Group className="mb-3">
                                <Form.Label>标题</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="title"
                                    value={formData.title}
                                    onChange={handleFormChange}
                                    placeholder="请输入图书标题"
                                    required
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>作者</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="author"
                                    value={formData.author}
                                    onChange={handleFormChange}
                                    placeholder="请输入作者"
                                    required
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>ISBN</Form.Label>
                                <Form.Control
                                    type="text"
                                    name="isbn"
                                    value={formData.isbn}
                                    onChange={handleFormChange}
                                    placeholder="请输入ISBN（相同ISBN = 多副本）"
                                />
                                <Form.Text className="text-muted">
                                    多本相同ISBN的书将被视为同一本书的不同副本
                                </Form.Text>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>库存数量</Form.Label>
                                <Form.Control
                                    type="number"
                                    name="count"
                                    value={formData.count}
                                    onChange={handleFormChange}
                                    min={1}
                                    required
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>封面图片</Form.Label>
                                <Form.Control
                                    type="file"
                                    accept="image/*"
                                    onChange={handleCoverUpload}
                                />
                                <Form.Text className="text-muted">
                                    支持 JPG、PNG 等图片格式，最大 5MB
                                </Form.Text>
                                {/* 封面预览 */}
                                {formData.cover && (
                                    <div className="mt-2 position-relative d-inline-block">
                                        <img
                                            src={formData.cover}
                                            alt="封面预览"
                                            className="cover-preview"
                                        />
                                        <Button
                                            variant="danger"
                                            size="sm"
                                            className="position-absolute top-0 end-0"
                                            style={{ transform: "translate(50%, -50%)", borderRadius: "50%" }}
                                            onClick={() => setFormData({ ...formData, cover: "" })}
                                        >
                                            X
                                        </Button>
                                    </div>
                                )}
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>简介</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    name="description"
                                    rows={3}
                                    value={formData.description}
                                    onChange={handleFormChange}
                                    placeholder="请输入图书简介（可选）"
                                />
                            </Form.Group>
                        </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => setShowModal(false)}>
                            取消
                        </Button>
                        <Button variant="primary" onClick={handleSave}>
                            {editingBook ? "保存修改" : "添加图书"}
                        </Button>
                    </Modal.Footer>
                </Modal>
            )}
        </Container>
    )
}

export default Books
