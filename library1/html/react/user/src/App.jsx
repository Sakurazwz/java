import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from 'react-bootstrap'
import { Routes, Route, Navigate } from 'react-router-dom'
import UserHeader from './pages/user/header/Header'
import Books from './pages/user/books/books'
import Borrow from './pages/user/borrow/borrow'
import History from './pages/user/history/history'
import Login from './pages/user/login/login'
import AddUser from './pages/user/adduser/adduser'
import Users from './pages/user/users/users'
import { authApi } from './services/api'
import './App.css'

// 普通用户认证检查
const ProtectedRoute = ({ children }) => {
    return authApi.isAuthenticated() ? children : <Navigate to="/login" replace />
}

// 未登录用户路由
const PublicRoute = ({ children }) => {
    return !authApi.isAuthenticated() ? children : <Navigate to="/" replace />
}

/**
 * 普通用户应用
 * 路由: /, /login, /register, /books, /borrow
 */
function App() {
    return (
        <div className="user-app">
            <UserHeader />
            <Container fluid className="p-0">
                <Routes>
                    <Route path="/login" element={
                        <PublicRoute>
                            <Login />
                        </PublicRoute>
                    } />
                    <Route path="/register" element={
                        <PublicRoute>
                            <AddUser />
                        </PublicRoute>
                    } />
                    <Route path="/" element={
                        <ProtectedRoute>
                            <Books />
                        </ProtectedRoute>
                    } />
                    <Route path="/books" element={
                        <ProtectedRoute>
                            <Books />
                        </ProtectedRoute>
                    } />
                    <Route path="/borrow" element={
                        <ProtectedRoute>
                            <Borrow />
                        </ProtectedRoute>
                    } />
                    <Route path="/history" element={
                        <ProtectedRoute>
                            <History />
                        </ProtectedRoute>
                    } />
                    <Route path="/users" element={
                        <ProtectedRoute>
                            <Users />
                        </ProtectedRoute>
                    } />
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </Container>
        </div>
    )
}

export default App
