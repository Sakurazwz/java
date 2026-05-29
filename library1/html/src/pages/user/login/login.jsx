import { useState } from "react"
import { Form, Button, Container, Alert } from "react-bootstrap"
import { Link, useNavigate } from "react-router-dom"
import { authApi } from "../../../services/api"
import "./login.css"

const Login = () => {
    const [formData, setFormData] = useState({
        name: "",
        password: "",
    })
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
                // 登录成功，跳转到首页
                navigate("/")
                window.location.reload()
            } else {
                setError("登录失败：未获取到令牌")
            }
        } catch (error) {
            setError(error.message || "登录失败")
        }
    }

    return (
        <Container className="mt-5" style={{ maxWidth: "400px" }}>
            <h2 className="text-center mb-4">用户登录</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="formName" className="mb-3">
                    <Form.Label>用户名</Form.Label>
                    <Form.Control
                        type="text"
                        name="name"
                        placeholder="请输入用户名"
                        value={formData.name}
                        onChange={handleInputChange}
                        required
                    />
                </Form.Group>
                <Form.Group controlId="formPassword" className="mb-3">
                    <Form.Label>密码</Form.Label>
                    <Form.Control
                        type="password"
                        name="password"
                        placeholder="请输入密码"
                        value={formData.password}
                        onChange={handleInputChange}
                        required
                    />
                </Form.Group>
                <div className="d-grid">
                    <Button variant="primary" type="submit">
                        登录
                    </Button>
                </div>
            </Form>
            <div className="text-center mt-3">
                还没有账号？<Link to="/register">立即注册</Link>
            </div>
        </Container>
    )
}

export default Login
