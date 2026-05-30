import { Link } from "react-router-dom";

function Navbar() {
    return (
        <nav className="navbar">
            <Link to="/" className="logo">
               <img src="/LLlogo.png" alt="Logo" className="nav-logo" />LiberLibrary
             </Link>
            <div className="nav-links">
                <Link to="/books">Книги</Link>
                <Link to="/authors">Авторы</Link>
                <Link to="/categories">Жанры</Link>
                <Link to="/upload-book">Опубликовать книгу</Link>
                <Link to="/profile">Учётная запись</Link>
            </div>
        </nav>
    );
}

export default Navbar;