// 全局 API 服务，封装所有后端接口调用
const API_BASE_URL = "http://localhost:8080/api"

// 获取 JWT Token
const getToken = () => localStorage.getItem("token")

// 设置 JWT Token
const setToken = (token) => {
    localStorage.setItem("token", token)
    // 解析并保存用户角色
    const payload = parseJwt(token)
    if (payload && payload.role) {
        localStorage.setItem("userRole", payload.role)
    }
}

// 解析 JWT payload
const parseJwt = (token) => {
    if (!token) return null
    try {
        const base64Url = token.split(".")[1]
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split("")
                .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
                .join("")
        )
        return JSON.parse(jsonPayload)
    } catch (e) {
        console.error("JWT解析失败:", e)
        return null
    }
}

// 从 JWT 获取当前用户信息（避免各组件重复解析）
const getCurrentUser = () => {
    const token = getToken()
    if (!token) return null
    const payload = parseJwt(token)
    if (!payload) return null
    return { id: payload.userId, name: payload.sub, role: payload.role }
}

// 获取用户角色
const getRole = () => localStorage.getItem("userRole")

// 判断是否为管理员
const isAdmin = () => {
    const role = getRole()
    return role === "ADMIN" || role === "ROLE_ADMIN"
}

// 清除 JWT Token
const removeToken = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("userRole")
}

// 请求拦截器
const fetchWithAuth = async (url, options = {}) => {
    const token = getToken()
    const headers = {
        "Content-Type": "application/json",
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
    }

    const response = await fetch(url, { ...options, headers })

    // 如果返回 401 或 403，跳转登录页
    if (response.status === 401 || response.status === 403) {
        removeToken()
        window.location.href = "/login"
        throw new Error("登录已过期，请重新登录")
    }

    return response
}

// 用户相关 API
export const authApi = {
    login: async (name, password) => {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: name, password }),
        })
        if (response.ok) {
            const data = await response.json()
            if (data.token) {
                setToken(data.token)
            }
            return data
        }
        throw new Error("登录失败")
    },

    logout: () => {
        removeToken()
    },

    isAuthenticated: () => !!getToken(),

    // 获取用户角色
    getRole,

    // 判断是否为管理员
    isAdmin,

    getCurrentUser,

    getToken,
    setToken,
    removeToken,
}

// 用户管理 API
export const userApi = {
    register: async (name, password) => {
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, password }),
        })
        if (!response.ok) {
            const error = await response.text()
            throw new Error(error)
        }
        return response.json()
    },

    getAllUsers: async (name) => {
        const url = name
            ? `${API_BASE_URL}/users/all?name=${encodeURIComponent(name)}`
            : `${API_BASE_URL}/users/all`
        const response = await fetchWithAuth(url)
        if (!response.ok) throw new Error("获取用户列表失败")
        return response.json()
    },

    deleteUser: async (id) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/users/delete/${id}`, {
            method: "DELETE",
        })
        if (!response.ok) {
            const error = await response.text()
            throw new Error(error)
        }
        return response.text()
    },

    updateRole: async (id, role) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/users/updateRole/${id}`, {
            method: "PUT",
            body: JSON.stringify({ role }),
        })
        if (!response.ok) {
            const error = await response.text()
            throw new Error(error)
        }
        return response.json()
    },
}

// 图书管理 API
export const bookApi = {
    getAllBooks: async () => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getAllBooks`)
        if (!response.ok) throw new Error("获取图书列表失败")
        return response.json()
    },

    getBookById: async (id) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getBookById/${id}`)
        if (!response.ok) throw new Error("获取图书信息失败")
        return response.json()
    },

    searchBooks: async (title) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getBookByTitle?title=${encodeURIComponent(title)}`)
        if (!response.ok) throw new Error("搜索失败")
        return response.json()
    },

    getBooksByIsbn: async (isbn) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getBooksByIsbn?isbn=${encodeURIComponent(isbn)}`)
        if (!response.ok) throw new Error("搜索ISBN失败")
        return response.json()
    },

    getCopyCountByIsbn: async (isbn) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/getCopyCountByIsbn?isbn=${encodeURIComponent(isbn)}`)
        if (!response.ok) return 0
        return response.json()
    },

    addBook: async (book) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/addBook`, {
            method: "POST",
            body: JSON.stringify(book),
        })
        if (!response.ok) throw new Error("添加图书失败")
        return response.json()
    },

    updateBook: async (book) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/updateBook`, {
            method: "PUT",
            body: JSON.stringify(book),
        })
        if (!response.ok) throw new Error("更新图书失败")
        return response.json()
    },

    deleteBook: async (id) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/deleteBook/${id}`, {
            method: "DELETE",
        })
        if (!response.ok) throw new Error("删除图书失败")
        return response.text()
    },

    recommend: async (query) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/books/recommend?query=${encodeURIComponent(query)}`)
        if (!response.ok) throw new Error("推荐请求失败")
        return response.json()
    },
}

// 借阅管理 API
export const borrowApi = {
    getAllBorrows: async ({ userId } = {}) => {
        const params = new URLSearchParams()
        if (userId) params.append("userId", userId)
        const qs = params.toString()
        const url = `${API_BASE_URL}/borrow/all${qs ? "?" + qs : ""}`
        const response = await fetchWithAuth(url)
        if (!response.ok) throw new Error("获取借阅记录失败")
        return response.json()
    },

    borrowBook: async (bookId, userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/add`, {
            method: "POST",
            body: JSON.stringify({ bookId, userId }),
        })
        if (!response.ok) throw new Error("借书失败")
        return response.text()
    },

    returnBook: async (bookId, userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/back`, {
            method: "DELETE",
            body: JSON.stringify({ bookId, userId }),
        })
        if (!response.ok) throw new Error("还书失败")
        return response.text()
    },

    renewBook: async (bookId, userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/updateBorrow`, {
            method: "POST",
            body: JSON.stringify({ bookId, userId }),
        })
        if (!response.ok) throw new Error("续借失败")
        return response.text()
    },

    getUserBorrows: async (userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/user?userId=${userId}`)
        if (!response.ok) throw new Error("获取用户借阅记录失败")
        return response.json()
    },

    getOverdueBorrows: async (userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrow/overdue?userId=${userId}`)
        if (!response.ok) throw new Error("获取逾期记录失败")
        return response.json()
    },
}

// 借阅历史 API
export const historyApi = {
    getAllHistory: async ({ userId, startDate, endDate } = {}) => {
        const params = new URLSearchParams()
        if (userId) params.append("userId", userId)
        if (startDate) params.append("startDate", startDate)
        if (endDate) params.append("endDate", endDate)
        const qs = params.toString()
        const url = `${API_BASE_URL}/borrowhistory/all${qs ? "?" + qs : ""}`
        const response = await fetchWithAuth(url)
        if (!response.ok) throw new Error("获取全部历史记录失败")
        return response.json()
    },

    getHistoryByBookId: async (bookId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrowhistory/getBorrowHistoryByBookId/${bookId}`)
        if (!response.ok) throw new Error("获取历史记录失败")
        return response.json()
    },

    getHistoryByUserId: async (userId) => {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrowhistory/getBorrowHistoryByUserId/${userId}`)
        if (!response.ok) throw new Error("获取用户历史记录失败")
        return response.json()
    },
}

export default { authApi, userApi, bookApi, borrowApi, historyApi }
