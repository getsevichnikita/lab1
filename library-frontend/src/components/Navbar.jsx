import { Link } from "react-router-dom";


function Navbar() {
    return (
        <nav className="navbar">
            <Link to="/" className="logo">
               <img src="/LLlogo.png" alt="Logo" className="nav-logo" />
               {' '}
                  LiberLibrary
             </Link>
            <div className="nav-links">
                <Link to="/books">Books</Link>
                <Link to="/authors">Authors</Link>
                <Link to="/categories">Categories</Link>
                <Link to="/upload-book">Upload Book</Link>
                <Link to="/login">Login</Link>
                <Link to="/profile">Profile</Link>
            </div>
        </nav>
    );
}

export default Navbar;