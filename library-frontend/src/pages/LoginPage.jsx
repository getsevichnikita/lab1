import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";

const API_URL = "https://library-api-v8wu.onrender.com";

function LoginPage({ onLogin }) {
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [password, setPassword] = useState("");

    const handleAuth = async () => {
        if (!name || !password) {
            toast.warning("Введите имя и пароль");
            return;
        }

        try {
            let res;

            try {
                res = await axios.post(`${API_URL}/auth/login`, {
                    name: name,
                    password
                });
                toast.success(`С возвращением, ${name}!`);
            } catch (e) {
                if (e.response?.status === 401 || e.response?.status === 404) {
                    toast.info("Аккаунт не найден, создаём новый...");
                    res = await axios.post(`${API_URL}/auth/register`, {
                        name: name,
                        password
                    });
                    toast.success(`Аккаунт создан! Добро пожаловать, ${name}!`);
                } else {
                    throw e;
                }
            }

            onLogin(res.data.id, name);
            navigate("/profile");

        } catch (error) {
            console.error(error);

            if (error.response?.status === 401) {
                toast.error("Неверный пароль. Попробуйте ещё раз.");
            } else if (error.response?.status === 409) {
                toast.error("Пользователь с таким именем уже существует.");
            } else if (error.response?.status === 500) {
                toast.error("Ошибка сервера. Попробуйте позже.");
            } else if (error.response == null) {
                toast.error("Нет соединения с сервером. Проверьте интернет.");
            } else {
                toast.error(error.response?.data?.message || "Ошибка авторизации");
            }
        }
    };

    return (
        <div className="page">
            <h1>Вход в библиотеку</h1>

            <div className="search-panel">
                <input
                    placeholder="Имя"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />

                <input
                    placeholder="Пароль"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") handleAuth();
                    }}
                />

                <button className="borrow-btn" onClick={handleAuth}>
                    Войти / Зарегистрироваться
                </button>
            </div>
        </div>
    );
}

export default LoginPage;