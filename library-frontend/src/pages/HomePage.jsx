import { Link } from "react-router-dom";

const readerId = localStorage.getItem("readerId");
const isLoggedIn = !!readerId;

function HomePage() {
    return (
        <div className="page" style={{ textAlign: "center", paddingTop: "60px" }}>
            <h1>Добро пожаловать в LiberLibrary</h1>
            <p style={{ fontSize: "18px", color: "#666", marginBottom: "40px" }}>
                LiberLibrary это свободная библиотека, в которой любой желающий может разместить свою книгу или же заказать чью-то
            </p>

            <div style={{ display: "flex", justifyContent: "center", gap: "20px", flexWrap: "wrap" }}>
                <Link to="/books" className="home-btn">Искать Книги</Link>
                <Link to="/authors" className="home-btn">Авторы</Link>
                <Link to="/categories">Жанры</Link>
                <Link to="/upload-book">Опубликовать книгу</Link>
                <Link to="/profile">Учётная запись</Link>
            </div>
        </div>
    );
}

export default HomePage;