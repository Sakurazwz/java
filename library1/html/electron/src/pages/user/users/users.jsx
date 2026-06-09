import { useState, useEffect, useCallback } from "react"
import { Table, Button, Container, Alert, Badge, Form, InputGroup } from "react-bootstrap"
import { userApi, authApi } from "../../../services/api"
import "./users.css"

const Users = () => {
    const [users, setUsers] = useState([])
    const [error, setError] = useState("")
    const [success, setSuccess] = useState("")
    const [searchTerm, setSearchTerm] = useState("")
    const isAdmin = authApi.isAdmin()
    const currentUserId = (() => {
        const user = authApi.getCurrentUser()
        return user ? user.id : null
    })()

    const loadUsers = useCallback(async (name) => {
        try {
            const data = await userApi.getAllUsers(name)
            setUsers(data)
        } catch (err) {
            setError("加载用户列表失败: " + err.message)
        }
    }, [])

    useEffect(() => {
        loadUsers()
    }, [loadUsers])

    const handleSearch = () => {
        loadUsers(searchTerm.trim() || undefined)
    }

    const handleSearchKeyDown = (e) => {
        if (e.key === "Enter") {
            handleSearch()
        }
    }

    const handleClearSearch = () => {
        setSearchTerm("")
        loadUsers()
    }

    const handleDelete = async (id, name) => {
        if (!window.confirm(`确认删除用户"${name}"吗？`)) return
        setError("")
        setSuccess("")
        try {
            await userApi.deleteUser(id)
            setSuccess(`用户"${name}"已删除`)
            loadUsers()
        } catch (err) {
            setError("删除失败: " + err.message)
        }
    }

    const handleToggleRole = async (id, currentRole) => {
        const newRole = currentRole === "ADMIN" ? "USER" : "ADMIN"
        const action = newRole === "ADMIN" ? "提升为管理员" : "降级为普通用户"
        if (!window.confirm(`确认${action}？`)) return
        setError("")
        setSuccess("")
        try {
            await userApi.updateRole(id, newRole)
            setSuccess("角色修改成功")
            loadUsers()
        } catch (err) {
            setError("修改角色失败: " + err.message)
        }
    }

    if (!isAdmin) {
        return (
            <Container className="mt-5">
                <div className="content-card">
                    <Alert variant="danger" className="mb-0">
                        无权限：仅管理员可访问用户管理
                    </Alert>
                </div>
            </Container>
        )
    }

    return (
        <Container className="mt-4">
            <div className="page-header">
                <h2>&#128101; 用户管理</h2>
                <Badge className="role-badge" bg="primary">
                    共 {users.length} 个用户
                </Badge>
            </div>

            <div className="content-card">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <InputGroup className="search-box" style={{ maxWidth: "360px" }}>
                        <Form.Control
                            placeholder="搜索用户名..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            onKeyDown={handleSearchKeyDown}
                        />
                        <Button variant="primary" onClick={handleSearch}>&#128269;</Button>
                        <Button variant="outline-secondary" onClick={handleClearSearch}>清除</Button>
                    </InputGroup>
                    <Button variant="outline-primary" size="sm" onClick={loadUsers}>
                        &#128472; 刷新
                    </Button>
                </div>

                {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}
                {success && <Alert variant="success" dismissible onClose={() => setSuccess("")}>{success}</Alert>}

                <Table hover>
                    <thead>
                        <tr>
                            <th style={{ width: "10%" }}>ID</th>
                            <th style={{ width: "25%" }}>用户名</th>
                            <th style={{ width: "20%" }}>角色</th>
                            <th style={{ width: "45%" }}>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user) => {
                            const isSelf = user.id === currentUserId
                            return (
                                <tr key={user.id}>
                                    <td><code>{user.id}</code></td>
                                    <td>
                                        <strong>{user.name}</strong>
                                        {isSelf && (
                                            <Badge bg="info" className="ms-2 self-badge">我</Badge>
                                        )}
                                    </td>
                                    <td>
                                        <Badge className="role-badge" bg={user.role === "ADMIN" ? "warning" : "primary"}>
                                            {user.role === "ADMIN" ? "管理员" : "普通用户"}
                                        </Badge>
                                    </td>
                                    <td>
                                        {isSelf ? (
                                            <span className="text-muted">当前登录用户</span>
                                        ) : (
                                            <>
                                                <Button
                                                    variant={user.role === "ADMIN" ? "outline-warning" : "outline-success"}
                                                    size="sm"
                                                    className="me-2"
                                                    onClick={() => handleToggleRole(user.id, user.role)}
                                                >
                                                    {user.role === "ADMIN" ? "降级" : "提升"}
                                                </Button>
                                                <Button
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => handleDelete(user.id, user.name)}
                                                >
                                                    删除
                                                </Button>
                                            </>
                                        )}
                                    </td>
                                </tr>
                            )
                        })}
                    </tbody>
                </Table>

                {users.length === 0 && !error && (
                    <div className="empty-state">
                        <div className="empty-state-icon">&#128269;</div>
                        <p>暂无用户数据</p>
                    </div>
                )}
            </div>
        </Container>
    )
}

export default Users
