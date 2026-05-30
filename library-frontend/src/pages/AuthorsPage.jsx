import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { getAuthors } from "../services/authorService";

function AuthorsPage() {
    const location = useLocation();
    const navigate = useNavigate();

    const params = new URLSearchParams(location.search);
    const name = params.get("name") || "";

    const [authors, setAuthors] = useState([]);

    useEffect(() => {
        loadData();
    }, [location.search]);

    const loadData = async () => {
        try {
            const data = await getAuthors();
            setAuthors(data);
        } catch (error) {
            console.error(error);
        }
    };

    const filtered = name
        ? authors.filter(a =>
            a.name.toLowerCase().includes(name.toLowerCase())
        )
        : authors;

    const handleAuthorClick = (authorName) => {
        navigate(`/?author=${encodeURIComponent(authorName)}`);
    };

    return (
        <div className="page">
            <h1>Authors</h1>

            <div className="books-grid">
                {filtered.map(a => (
                    <div
                        key={a.id}
                        className="book-card"
                        onClick={() => handleAuthorClick(a.name)}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) => { if (e.key === 'Enter') handleAuthorClick(a.name); }}
                    >
                        {a.name}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default AuthorsPage;