import { useEffect, useState } from "react";

import {
    getCategories
} from "../services/categoryService";

import {
    getBooks
} from "../services/bookService";

function CategoriesPage() {

    const [categories, setCategories] =
        useState([]);

    const [books, setBooks] =
        useState([]);

    useEffect(() => {

        loadData();

    }, []);

    const loadData = async () => {

        try {

            const categoriesData =
                await getCategories();

            const booksData =
                await getBooks();

            setCategories(categoriesData);

            setBooks(booksData);

        } catch (error) {

            console.error(error);
        }
    };

    const getBookTitles = (bookIds) => {

        return books
            .filter(book =>
                bookIds?.includes(book.id)
            )
            .map(book => book.title)
            .join(", ");
    };

    return (

        <div className="page">

            <h1>
                Categories
            </h1>

            <div className="books-grid">

                {categories.map(category => (

                    <div
                        className="book-card"
                        key={category.id}
                    >

                        <h2>
                            {category.name}
                        </h2>

                        <p>

                            <strong>
                                Books:
                            </strong>

                        </p>

                        <p>
                            {
                                getBookTitles(
                                    category.bookIds
                                )
                            }
                        </p>

                    </div>
                ))}

            </div>

        </div>
    );
}

export default CategoriesPage;
