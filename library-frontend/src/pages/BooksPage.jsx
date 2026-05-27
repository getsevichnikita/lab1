import { useEffect, useState } from "react";

import {
    getBooks,
    searchBooksByAuthor
} from "../services/bookService";

import {
    getAuthors
} from "../services/authorService";

import {
    getCategories
} from "../services/categoryService";

function BooksPage() {

    const [books, setBooks] = useState([]);

    const [authors, setAuthors] = useState([]);

    const [categories, setCategories] = useState([]);

    const [searchAuthor, setSearchAuthor] =
        useState("");

    useEffect(() => {

        loadData();

    }, []);

    const loadData = async () => {

        try {

            const booksData =
                await getBooks();

            const authorsData =
                await getAuthors();

            const categoriesData =
                await getCategories();

            setBooks(booksData);

            setAuthors(authorsData);

            setCategories(categoriesData);

        } catch (error) {

            console.error(error);
        }
    };

    const handleSearch = async () => {

        try {

            if (searchAuthor.trim() === "") {

                loadData();

                return;
            }

            const result =
                await searchBooksByAuthor(
                    searchAuthor
                );

            setBooks(result);

        } catch (error) {

            console.error(error);
        }
    };

    const getAuthorNames = (authorIds) => {

        return authors
            .filter(author =>
                authorIds?.includes(author.id)
            )
            .map(author => author.name)
            .join(", ");
    };

    const getCategoryNames = (categoryIds) => {

        return categories
            .filter(category =>
                categoryIds?.includes(category.id)
            )
            .map(category => category.name)
            .join(", ");
    };

    return (

        <div className="page">

            <h1>
                Library Books
            </h1>

            <div className="search-panel">

                <input
                    type="text"
                    placeholder="Search by author..."
                    value={searchAuthor}
                    onChange={(e) =>
                        setSearchAuthor(
                            e.target.value
                        )
                    }
                />

                <button onClick={handleSearch}>
                    Search
                </button>

            </div>

            <div className="books-grid">

                {books.map(book => (

                    <div
                        className="book-card"
                        key={book.id}
                    >

                        <h2>
                            {book.title}
                        </h2>

                        <p>
                            <strong>
                                Year:
                            </strong>

                            {" "}
                            {book.publicationYear}
                        </p>

                        <p>
                            <strong>
                                Authors:
                            </strong>

                            {" "}
                            {
                                getAuthorNames(
                                    book.authorIds
                                )
                            }
                        </p>

                        <p>
                            <strong>
                                Categories:
                            </strong>

                            {" "}
                            {
                                getCategoryNames(
                                    book.categoryIds
                                )
                            }
                        </p>

                    </div>
                ))}

            </div>

        </div>
    );
}

export default BooksPage;
