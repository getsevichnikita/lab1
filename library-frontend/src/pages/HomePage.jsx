import { Link } from "react-router-dom";

const readerId = localStorage.getItem("readerId");
const isLoggedIn = !!readerId;

function HomePage() {
    return (
        <div className="page" style={{ textAlign: "center", paddingTop: "60px" }}>
            <h1>Welcome to LiberLibrary</h1>
            <p style={{ fontSize: "18px", color: "#666", marginBottom: "40px" }}>
                Your personal library management system
            </p>

            <div style={{ display: "flex", justifyContent: "center", gap: "20px", flexWrap: "wrap" }}>
                <Link to="/books" className="home-btn">Browse Books</Link>
                <Link to="/authors" className="home-btn">Authors</Link>
                <Link to="/categories" className="home-btn">Categories</Link>
                {!isLoggedIn && <Link to="/login" className="home-btn">Login</Link>}
                {isLoggedIn && <Link to="/profile" className="home-btn">My Profile</Link>}
                {isLoggedIn && <Link to="/upload-book" className="home-btn">Upload Book</Link>}
            </div>
        </div>
    );
}

export default HomePage;