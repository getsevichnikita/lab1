import { useEffect, useState } from "react";

import {
    getAuthors
} from "../services/authorService";

import {
    getBooks
} from "../services/bookService";

function AuthorsPage() {

    const [authors, setAuthors] =
        useState([]);

    const [books, setBooks] =
        useState([]);

    useEffect(() => {

        loadData();

    }, []);

    const loadData = async () => {

        try {

            const authorsData =
                await getAuthors();

            const booksData =
                await getBooks();

            setAuthors(authorsData);

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
                Authors
            </h1>

            <div className="books-grid">

                {authors.map(author => (

                    <div
                        className="book-card"
                        key={author.id}
                    >

                        <h2>
                            {author.name}
                        </h2>

                        <p>

                            <strong>
                                Books:
                            </strong>

                        </p>

                        <p>
                            {
                                getBookTitles(
                                    author.bookIds
                                )
                            }
                        </p>

                    </div>
                ))}

            </div>

        </div>
    );
}

export default AuthorsPage;
