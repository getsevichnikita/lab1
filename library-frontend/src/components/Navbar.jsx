import { Link } from "react-router-dom";

function Navbar() {
    const readerId = localStorage.getItem("readerId");
    const isLoggedIn = !!readerId;

    return (
        <nav className="navbar">
            <Link to="/" className="logo">LiberLibrary</Link>
            <div className="nav-links">
                <Link to="/books">Books</Link>
                <Link to="/authors">Authors</Link>
                <Link to="/categories">Categories</Link>
                {isLoggedIn && <Link to="/upload-book">Upload Book</Link>}
                {isLoggedIn ? (
                    <Link to="/profile">Profile</Link>
                ) : (
                    <Link to="/login">Login</Link>
                )}
            </div>
        </nav>
    );
}

export default Navbar;