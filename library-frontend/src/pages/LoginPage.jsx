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
            toast.warning("Please enter both name and password");
            return;
        }

        try {
            let res;

            try {
                res = await axios.post(`${API_URL}/auth/login`, {
                    name: name,
                    password
                });
                toast.success(`Welcome back, ${name}!`);
            } catch (e) {
                if (e.response?.status === 401 || e.response?.status === 404) {
                    toast.info("Account not found, creating new one...");
                    res = await axios.post(`${API_URL}/auth/register`, {
                        name: name,
                        password
                    });
                    toast.success(`Account created! Welcome, ${name}!`);
                } else {
                    throw e;
                }
            }

            onLogin(res.data.id, name);
            navigate("/profile");

        } catch (error) {
            console.error(error);

            if (error.response?.status === 401) {
                toast.error("Invalid password. Please try again.");
            } else if (error.response?.status === 409) {
                toast.error("User with this name already exists.");
            } else if (error.response?.status === 500) {
                toast.error("Server error. Please try again later.");
            } else if (error.response==null) {
                toast.error("Cannot connect to server. Check your internet connection.");
            } else {
                toast.error(error.response?.data?.message || "Authentication failed");
            }
        }
    };

    return (
        <div className="page">
            <h1>Library Login</h1>

            <div className="search-panel">
                <input
                    placeholder="Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />

                <input
                    placeholder="Password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") handleAuth();
                    }}
                />

                <button className="borrow-btn" onClick={handleAuth}>
                    Register / Login
                </button>
            </div>
        </div>
    );
}

export default LoginPage;