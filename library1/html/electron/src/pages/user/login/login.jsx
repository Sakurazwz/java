import { useState } from "react"
import { Form, Button, Container, Alert } from "react-bootstrap"
import { Link, useNavigate } from "react-router-dom"
import { authApi } from "../../../services/api"
import "./login.css"

const Login = () => {
    const [formData, setFormData] = useState({ name: "", password: "" })
    const [error, setError] = useState("")
    const navigate = useNavigate()

    const handleInputChange = (e) => {
        const { name, value } = e.target
        setFormData({ ...formData, [name]: value })
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError("")

        if (!formData.name.trim() || !formData.password.trim()) {
            setError("用户名和密码不能为空")
            return
        }

        try {
            const data = await authApi.login(formData.name, formData.password)
            if (data.token) {
                navigate("/")
            } else {
                setError("登录失败：未获取到令牌")
            }
        } catch (error) {
            setError(error.message || "登录失败")
        }
    }

    return (
        <Container className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <h2>&#128272; 欢迎回来</h2>
                    <p className="text-white-50">登录图书管理系统</p>
                </div>
                <div className="auth-body">
                    {error && <Alert variant="danger">{error}</Alert>}
                    <Form onSubmit={handleSubmit}>
                        <Form.Group controlId="formName" className="mb-3">
                            <Form.Label>&#128100; 用户名</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                placeholder="请输入用户名"
                                value={formData.name}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group controlId="formPassword" className="mb-4">
                            <Form.Label>&#128274; 密码</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                placeholder="请输入密码"
                                value={formData.password}
                                onChange={handleInputChange}
                                required
                            />
                        </Form.Group>
                        <div className="d-grid mb-3">
                            <Button variant="primary" type="submit" size="lg">
                                登录
                            </Button>
                        </div>
                    </Form>
                    <div className="text-center">
                        <span className="text-muted">还没有账号？</span>
                        <Link to="/register" className="ms-1">立即注册</Link>
                    </div>
                </div>
            </div>
        </Container>
    )
}

export default Login
