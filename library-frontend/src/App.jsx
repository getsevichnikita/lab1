import { useState } from "react";
import {
    BrowserRouter,
    Routes,
    Route,
    Navigate
} from "react-router-dom";

import Navbar from "./components/Navbar";

import BooksPage from "./pages/BooksPage";
import AuthorsPage from "./pages/AuthorsPage";
import CategoriesPage from "./pages/CategoriesPage";
import ProfilePage from "./pages/ProfilePage";
import LoginPage from "./pages/LoginPage";

function App() {

    const [readerId, setReaderId] = useState(
        () => {
            const saved = localStorage.getItem("readerId");
            return saved ? Number(saved) : null;
        }
    );

    const login = (id) => {
        setReaderId(id);
        localStorage.setItem("readerId", id);
        localStorage.setItem("readerName", user.name);

    };

    const logout = () => {
        setReaderId(null);
        localStorage.removeItem("readerId");
    };

    return (

        <BrowserRouter>

            <Navbar />

            <Routes>

                <Route
                    path="/"
                    element={<BooksPage />}
                />

                <Route
                    path="/authors"
                    element={<AuthorsPage />}
                />

                <Route
                    path="/categories"
                    element={<CategoriesPage />}
                />

                <Route
                    path="/login"
                    element={
                        <LoginPage onLogin={login} />
                    }
                />

                <Route
                    path="/profile"
                    element={
                        readerId ? (
                            <ProfilePage
                                readerId={readerId}
                                    readerName={localStorage.getItem("readerName")}
                                onLogout={logout}
                            />
                        ) : (
                            <Navigate to="/login" />
                        )
                    }
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;