1
import { Link } from "react-router-dom";

function Navbar() {

    return (

        <nav className="navbar">

            <div className="logo">
                Electronic Library
            </div>

            <div className="nav-links">

                <Link to="/">
                    Books
                </Link>

                <Link to="/authors">
                    Authors
                </Link>

                <Link to="/categories">
                    Categories
                </Link>

                <Link to="/profile">
                    Profile
                </Link>

            </div>

        </nav>
    );
}

export default Navbar;
