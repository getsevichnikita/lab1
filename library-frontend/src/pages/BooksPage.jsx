import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";
import { getBooks } from "../services/bookService";
const API_URL = "https://library-api-v8wu.onrender.com";

function BooksPage() {
    const [pdfUrl, setPdfUrl] = useState(null);
    const [books, setBooks] = useState([]);
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
            setBooks(Array.isArray(booksData) ? booksData : []);
        } catch (error) {
            console.error(error);
        }
    };


    const filteredBooks = (books || []).filter((book) => {
        const authorNames = (book.authors || []).map((a) =>
            a.name.toLowerCase()
        );
        const categoryNames = (book.categories || []).map((c) =>
            c.name.toLowerCase()
        );

        const matchesTitle =
            !searchTitle ||
            book.title?.toLowerCase().includes(searchTitle.toLowerCase());

        const matchesAuthor =
            !searchAuthor ||
            authorNames.some((name) => name.includes(searchAuthor.toLowerCase()));

        const matchesCategory =
            !searchCategory ||
            categoryNames.some((name) =>
                name.includes(searchCategory.toLowerCase())
            );

        const matchesYearFrom =
            !yearFrom || book.publicationYear >= Number(yearFrom);

        const matchesYearTo =
            !yearTo || book.publicationYear <= Number(yearTo);

        const matchesUrlAuthor =
            !authorFilter ||
            authorNames.some((name) =>
                name.includes(authorFilter.toLowerCase())
            );

        const matchesUrlCategory =
            !categoryFilter ||
            categoryNames.some((name) =>
                name.includes(categoryFilter.toLowerCase())
            );

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
            toast.info("Login first");
            return;
        }

        const today = new Date();
        const returnDate = new Date();
        returnDate.setDate(today.getDate() + 14);

        try {
            await axios.post(`${API_URL}/loans`, {
                readerId: Number(readerId),
                bookId: Number(bookId),
                issueDate: today.toISOString().split("T")[0],
                returnDate: returnDate.toISOString().split("T")[0],
            });
            toast.success("Book borrowed");
        } catch (error) {
            console.error(error);
            toast.error(
                error.response?.data?.message || "Cannot borrow book"
            );
        }
    };

    const closeModal = () => {
        setSelectedBook(null);
        if (pdfUrl) {
            URL.revokeObjectURL(pdfUrl);
            setPdfUrl(null);
        }
    };

    const openPdf = async (bookId) => {
        const blob = await axios
            .get(`${API_URL}/books/${bookId}/pdf`, {
                responseType: "blob",
            })
            .then((res) => res.data);
        const url = window.URL.createObjectURL(blob);
        window.open(url, "_blank");
    };

    const downloadPdf = async (bookId, title) => {
        const blob = await axios
            .get(`${API_URL}/books/${bookId}/pdf`, {
                responseType: "blob",
            })
            .then((res) => res.data);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${title}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
    };

    return (
        <div className="page">
            <h1>Books</h1>

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

            <div className="books-grid">
                {filteredBooks.map((book) => (
                    <div
                        key={book.id}
                        className="book-card"
                        onClick={() => setSelectedBook(book)}
                    >
                        <h2>{book.title}</h2>
                        <p>
                            <strong>Year:</strong> {book.publicationYear}
                        </p>
                        <p>
                            <strong>Authors:</strong>{" "}
                            {(book.authors || [])
                                .map((a) => a.name)
                                .join(", ")}
                        </p>
                        <p>
                            <strong>Categories:</strong>{" "}
                            {(book.categories || [])
                                .map((c) => c.name)
                                .join(", ")}
                        </p>
                    </div>
                ))}
            </div>

            {selectedBook && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div
                        className="modal"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <div className="modal-header">
                            <h2>{selectedBook.title}</h2>
                            <button className="close-btn" onClick={closeModal}>
                                ✕
                            </button>
                        </div>

                        <div className="modal-body">
                            <p>
                                <strong>Year:</strong>{" "}
                                {selectedBook.publicationYear}
                            </p>
                            <p>
                                <strong>Authors:</strong>{" "}
                                {(selectedBook.authors || [])
                                    .map((a) => a.name)
                                    .join(", ")}
                            </p>
                            <p>
                                <strong>Categories:</strong>{" "}
                                {(selectedBook.categories || [])
                                    .map((c) => c.name)
                                    .join(", ")}
                            </p>

                            <div className="modal-actions">
                                <button
                                    className="borrow-btn"
                                    onClick={() =>
                                        borrowBook(selectedBook.id)
                                    }
                                >
                                    Borrow
                                </button>
                                <button
                                    className="borrow-btn"
                                    onClick={() => openPdf(selectedBook.id)}
                                >
                                    View PDF
                                </button>
                                <button
                                    className="borrow-btn"
                                    onClick={() =>
                                        downloadPdf(
                                            selectedBook.id,
                                            selectedBook.title
                                        )
                                    }
                                >
                                    Download PDF
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default BooksPage;