import { useState } from "react"
import "./adduser.css"
import { Form, Button, Container, Alert } from "react-bootstrap"
import { Link, useNavigate } from "react-router-dom"
import { userApi } from "../../../services/api"

const AddUser = () => {
    const [formData, setFormData] = useState({
        name: "",
        password: "",
    })
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
            setTimeout(() => {
                navigate("/login")
            }, 1500)
        } catch (error) {
            setError(error.message)
        }
    }

    return (
        <Container className="mt-5" style={{ maxWidth: "400px" }}>
            <h2 className="text-center mb-4">用户注册</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {success && <Alert variant="success">{success}</Alert>}
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
                        注册
                    </Button>
                </div>
            </Form>
            <div className="text-center mt-3">
                已有账号？<Link to="/login">立即登录</Link>
            </div>
        </Container>
    )
}

export default AddUser
