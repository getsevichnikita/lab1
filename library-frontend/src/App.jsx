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

    const [readerId, setReaderId] = useState(null);

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
                        <LoginPage
                            onLogin={setReaderId}
                        />
                    }
                />

                <Route
                    path="/profile"
                    element={
                        readerId ? (
                            <ProfilePage readerId={readerId} />
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

