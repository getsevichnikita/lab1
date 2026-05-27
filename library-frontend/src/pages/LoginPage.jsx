import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function LoginPage({ onLogin }) {
    const navigate = useNavigate();
    const [name, setName] = useState("");
    const [password, setPassword] = useState("");

const handleAuth = async () => {

    if (!name || !password) return;

     try {
               let res;

               try {
                   res = await axios.post("http://localhost:8080/auth/login", {
                       username: name,
                       password
                   });
               } catch (e) {
                   res = await axios.post("http://localhost:8080/auth/register", {
                       username: name,
                       password
                   });
               }

               onLogin(res.data.id);
               navigate("/profile");

           } catch (error) {
               console.error(error);
           }
       };

    return (

        <div className="page">

            <h1>Library Login</h1>

            <div className="search-panel">

                <input
                    placeholder="Name"
                    value={name}
                    onChange={(e) =>
                        setName(e.target.value)
                    }
                />

                <input
                    placeholder="Password"
                    type="password"
                    value={password}
                    onChange={(e) =>
                        setPassword(e.target.value)
                    }
                />

                <button onClick={handleAuth}>
                    Register / Login
                </button>

            </div>

        </div>
    );
}

export default LoginPage;
