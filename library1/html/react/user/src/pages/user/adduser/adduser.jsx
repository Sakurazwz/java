import { useState } from "react"
import "./adduser.css"
import { Form, Button, Container, Alert } from "react-bootstrap"
import { Link, useNavigate } from "react-router-dom"
import { userApi } from "../../../services/api"

const AddUser = () => {
    const [formData, setFormData] = useState({ name: "", password: "" })
    const [error, setError] = useState("")
    const [success, setSuccess] = useState("")
    const navigate = useNavigate()

    const handleInputChange = (e) => {
        const { name, value } = e.target
        setFormData({ ...formData, [name]: value })
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError("")
        setSuccess("")

        try {
            await userApi.register(formData.name, formData.password)
            setSuccess("注册成功！即将跳转到登录页...")
            setTimeout(() => navigate("/login"), 1500)
        } catch (error) {
            setError(error.message)
        }
    }

    return (
        <Container className="auth-container">
            <div className="auth-card">
                <div className="auth-header">
                    <h2>&#128100; 创建账号</h2>
                    <p className="text-white-50">注册一个新的图书馆账号</p>
                </div>
                <div className="auth-body">
                    {error && <Alert variant="danger">{error}</Alert>}
                    {success && <Alert variant="success">{success}</Alert>}
                    <Form onSubmit={handleSubmit}>
                        <Form.Group controlId="formName" className="mb-3">
                            <Form.Label>&#10002; 用户名</Form.Label>
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
                                placeholder="至少6位密码"
                                value={formData.password}
                                onChange={handleInputChange}
                                required
                                minLength={6}
                            />
                        </Form.Group>
                        <div className="d-grid mb-3">
                            <Button variant="primary" type="submit" size="lg">
                                注册
                            </Button>
                        </div>
                    </Form>
                    <div className="text-center">
                        <span className="text-muted">已有账号？</span>
                        <Link to="/login" className="ms-1">立即登录</Link>
                    </div>
                </div>
            </div>
        </Container>
    )
}

export default AddUser
