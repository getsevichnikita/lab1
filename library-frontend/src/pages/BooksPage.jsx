import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";

import { getBooks } from "../services/bookService";
import { getAuthors } from "../services/authorService";
import { getCategories } from "../services/categoryService";

function BooksPage() {

    const [books, setBooks] = useState([]);
    const [authors, setAuthors] = useState([]);
    const [categories, setCategories] = useState([]);

    const [selectedBook, setSelectedBook] = useState(null);

    const [searchTitle, setSearchTitle] = useState("");
    const [searchAuthor, setSearchAuthor] = useState("");
    const [searchCategory, setSearchCategory] = useState("");
    const [yearFrom, setYearFrom] = useState("");
    const [yearTo, setYearTo] = useState("");

    const location = useLocation();
    const params = new URLSearchParams(location.search);

    const authorFilter = params.get("author") || "";
    const categoryFilter = params.get("category") || "";

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const booksData = await getBooks();
            const authorsData = await getAuthors();
            const categoriesData = await getCategories();

            setBooks(booksData || []);
            setAuthors(authorsData || []);
            setCategories(categoriesData || []);

        } catch (error) {
            console.error(error);
        }
    };

    const getAuthorNames = (authorIds = []) => {
        return (authors || [])
            .filter(a => authorIds?.includes(a.id))
            .map(a => a.name)
            .join(", ");
    };

    const getCategoryNames = (categoryIds = []) => {
        return (categories || [])
            .filter(c => categoryIds?.includes(c.id))
            .map(c => c.name)
            .join(", ");
    };

    const filteredBooks = (books || []).filter(book => {

        const authorNames =
            getAuthorNames(book.authorIds || []).toLowerCase();

        const categoryNames =
            getCategoryNames(book.categoryIds || []).toLowerCase();

        const matchesTitle =
            !searchTitle ||
            book.title?.toLowerCase().includes(searchTitle.toLowerCase());

        const matchesAuthor =
            !searchAuthor ||
            authorNames.includes(searchAuthor.toLowerCase());

        const matchesCategory =
            !searchCategory ||
            categoryNames.includes(searchCategory.toLowerCase());

        const matchesYearFrom =
            !yearFrom ||
            book.publicationYear >= Number(yearFrom);

        const matchesYearTo =
            !yearTo ||
            book.publicationYear <= Number(yearTo);

        const matchesUrlAuthor =
            !authorFilter ||
            authorNames.includes(authorFilter.toLowerCase());

        const matchesUrlCategory =
            !categoryFilter ||
            categoryNames.includes(categoryFilter.toLowerCase());

        return (
            matchesTitle &&
            matchesAuthor &&
            matchesCategory &&
            matchesYearFrom &&
            matchesYearTo &&
            matchesUrlAuthor &&
            matchesUrlCategory
        );
    });

    const borrowBook = async (bookId) => {

        const readerId = localStorage.getItem("readerId");

        if (!readerId) {
            alert("Login first");
            return;
        }

        const today = new Date();
        const returnDate = new Date();
        returnDate.setDate(today.getDate() + 14);

        try {
            await axios.post("http://localhost:8080/loans", {
                readerId: Number(readerId),
                bookId: Number(bookId),
                issueDate: today.toISOString().split("T")[0],
                returnDate: returnDate.toISOString().split("T")[0]
            });

            alert("Book borrowed");

        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="page">

            <h1>Library Books</h1>

            <div className="search-panel">

                <input
                    placeholder="Title..."
                    value={searchTitle}
                    onChange={(e) => setSearchTitle(e.target.value)}
                />

                <input
                    placeholder="Author..."
                    value={searchAuthor}
                    onChange={(e) => setSearchAuthor(e.target.value)}
                />

                <input
                    placeholder="Category..."
                    value={searchCategory}
                    onChange={(e) => setSearchCategory(e.target.value)}
                />

                <input
                    type="number"
                    placeholder="Year from..."
                    value={yearFrom}
                    onChange={(e) => setYearFrom(e.target.value)}
                />

                <input
                    type="number"
                    placeholder="Year to..."
                    value={yearTo}
                    onChange={(e) => setYearTo(e.target.value)}
                />

            </div>

            {/* BOOKS */}
            <div className="books-grid">

                {filteredBooks.map(book => (

                    <div
                        key={book.id}
                        className="book-card"
                        onClick={() => setSelectedBook(book)}
                    >
                        <h2>{book.title}</h2>

                        <p><strong>Year:</strong> {book.publicationYear}</p>

                        <p>
                            <strong>Authors:</strong>{" "}
                            {getAuthorNames(book.authorIds)}
                        </p>

                        <p>
                            <strong>Categories:</strong>{" "}
                            {getCategoryNames(book.categoryIds)}
                        </p>

                    </div>
                ))}

       </div>


            {/* MODAL */}
            {selectedBook && (
                <div
                    className="modal-overlay"
                    onClick={() => setSelectedBook(null)}
                >

                    <div
                        className="modal"
                        onClick={(e) => e.stopPropagation()}
                    >

                        <div className="modal-header">
                            <h2>{selectedBook.title}</h2>

                            <button
                                className="close-btn"
                                onClick={() => setSelectedBook(null)}
                            >
                                ✕
                            </button>
                        </div>

                        <div className="modal-body">

                            <p><strong>Year:</strong> {selectedBook.publicationYear}</p>

                            <p><strong>Authors:</strong> {getAuthorNames(selectedBook.authorIds)}</p>

                            <p><strong>Categories:</strong> {getCategoryNames(selectedBook.categoryIds)}</p>

                        </div>

                        <div className="modal-actions">

                            <button
                                className="borrow-btn"
                                onClick={() => borrowBook(selectedBook.id)}
                            >
                                Borrow
                            </button>

                        </div>

                    </div>

                </div>
            )}

        </div>
    );
}

export default BooksPage;