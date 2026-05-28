import { useState } from "react";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import UploadBookPage from "./pages/UploadBookPage";
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

const [readerName, setReaderName] = useState(
    () => localStorage.getItem("readerName")
);
    const login = (id, name) => {
        setReaderId(id);
        setReaderName(name);
        localStorage.setItem("readerId", id);
        localStorage.setItem("readerName", name);
    };

  const logout = () => {
      setReaderId(null);
      setReaderName(null);
      localStorage.removeItem("readerId");
      localStorage.removeItem("readerName");
  };

    return (

        <BrowserRouter>
    <ToastContainer
                position="bottom-left"
                autoClose={3000}
                hideProgressBar={false}
                newestOnTop
                closeOnClick
                pauseOnHover
                draggable
            />
            <Navbar />

            <Routes>

                <Route
                    path="/"
                    element={<BooksPage />}
                />

                <Route
                        path="/upload-book"
                        element={<UploadBookPage />}
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
                                readerName={readerName}
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