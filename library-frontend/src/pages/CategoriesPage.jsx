import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { getCategories } from "../services/categoryService";

function CategoriesPage() {
    const location = useLocation();
    const navigate = useNavigate();

    const params = new URLSearchParams(location.search);
    const name = params.get("name") || "";

    const [categories, setCategories] = useState([]);

    useEffect(() => {
        loadData();
    }, [location.search]);

    const loadData = async () => {
        try {
            const data = await getCategories();
            setCategories(data);
        } catch (error) {
            console.error(error);
        }
    };

    const filtered = name
        ? categories.filter(c =>
            c.name.toLowerCase().includes(name.toLowerCase())
        )
        : categories;

    const handleCategoryClick = (categoryName) => {
        navigate(`/?category=${encodeURIComponent(categoryName)}`);
    };

    return (
        <div className="page">
            <h1>Categories</h1>

            <div className="books-grid">
                {filtered.map(c => (
                    <div
                        key={c.id}
                        className="book-card"
                        onClick={() => handleCategoryClick(c.name)}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) => { if (e.key === 'Enter') handleCategoryClick(c.name); }}
                    >
                        {c.name}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default CategoriesPage;