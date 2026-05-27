import { Nav, Badge } from "react-bootstrap"
import { Navbar, Container, Button } from "react-bootstrap"
import { Link, useNavigate } from "react-router-dom"
import { authApi } from "../../../services/api"
import "./Header.css"

const UserHeader = () => {
    const navigate = useNavigate()
    const isAuthenticated = authApi.isAuthenticated()
    // 直接从 localStorage 读取，登录/登出后自动反映最新角色
    const isAdmin = authApi.isAdmin()

    const handleLogout = () => {
        authApi.logout()
        navigate("/login")
    }

    return (
        <Navbar bg="primary" variant="dark" expand="lg">
            <Container>
                <Navbar.Brand as={Link} to="/">
                    <strong>图书管理系统</strong>
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {isAuthenticated && (
                            <>
                                <Nav.Link as={Link} to="/books">图书管理</Nav.Link>
                                <Nav.Link as={Link} to="/borrow">借阅管理</Nav.Link>
                            </>
                        )}
                    </Nav>
                    <Nav className="d-flex align-items-center gap-2">
                        {!isAuthenticated ? (
                            <>
                                <Nav.Link as={Link} to="/login">登录</Nav.Link>
                                <Nav.Link as={Link} to="/register">注册</Nav.Link>
                            </>
                        ) : (
                            <>
                                <Badge bg={"info"}>
                                    {isAdmin ? "管理员" : "普通用户 - 只读模式"}
                                </Badge>
                                <Button variant="outline-light" size="sm" onClick={handleLogout}>
                                    退出
                                </Button>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    )
}

export default UserHeader
