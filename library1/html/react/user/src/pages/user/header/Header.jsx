import { Nav, Badge } from "react-bootstrap"
import { Navbar, Container, Button } from "react-bootstrap"
import { Link, useNavigate, useLocation } from "react-router-dom"
import { authApi } from "../../../services/api"
import "./Header.css"

const UserHeader = () => {
    const navigate = useNavigate()
    const location = useLocation()
    const isAuthenticated = authApi.isAuthenticated()
    const isAdmin = authApi.isAdmin()

    const handleLogout = () => {
        authApi.logout()
        navigate("/login")
    }

    const isActive = (path) => location.pathname === path ? "active" : ""

    return (
        <Navbar className="app-navbar" expand="lg" variant="dark">
            <Container>
                <Navbar.Brand as={Link} to="/" className="brand-text">
                    <span className="brand-icon">&#128218;</span> 图书管理系统
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {isAuthenticated && (
                            <>
                                <Nav.Link
                                    as={Link}
                                    to="/books"
                                    className={`nav-link-custom ${isActive("/books") || isActive("/")}`}
                                >
                                    &#128214; 图书管理
                                </Nav.Link>
                                <Nav.Link
                                    as={Link}
                                    to="/borrow"
                                    className={`nav-link-custom ${isActive("/borrow")}`}
                                >
                                    &#128220; 借阅管理
                                </Nav.Link>
                                <Nav.Link
                                    as={Link}
                                    to="/history"
                                    className={`nav-link-custom ${isActive("/history")}`}
                                >
                                    &#128337; 借阅历史
                                </Nav.Link>
                                {isAdmin && (
                                    <Nav.Link
                                        as={Link}
                                        to="/users"
                                        className={`nav-link-custom ${isActive("/users")}`}
                                    >
                                        &#128101; 用户管理
                                    </Nav.Link>
                                )}
                            </>
                        )}
                    </Nav>
                    <Nav className="d-flex align-items-center gap-2">
                        {!isAuthenticated ? (
                            <>
                                <Nav.Link as={Link} to="/login" className="nav-link-custom">登录</Nav.Link>
                                <Nav.Link as={Link} to="/register" className="nav-link-custom">注册</Nav.Link>
                            </>
                        ) : (
                            <>
                                <Badge className="role-badge" bg={isAdmin ? "warning" : "primary"}>
                                    {isAdmin ? "管理员" : "普通用户"}
                                </Badge>
                                <Button className="btn-logout" variant="outline-light" size="sm" onClick={handleLogout}>
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
