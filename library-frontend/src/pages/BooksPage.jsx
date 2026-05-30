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
    const [bookStats, setBookStats] = useState(null);

    const [searchTitle, setSearchTitle] = useState("");
    const [searchAuthor, setSearchAuthor] = useState("");
    const [searchCategory, setSearchCategory] = useState("");
    const [yearFrom, setYearFrom] = useState("");
    const [yearTo, setYearTo] = useState("");

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const authorFilter = params.get("author") || "";
    const categoryFilter = params.get("category") || "";

    const readerId = localStorage.getItem("readerId");
    const isLoggedIn = !!readerId;

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const booksData = await getBooks();
            const allBooks = Array.isArray(booksData) ? booksData : (booksData.content || []);
            const grouped = groupBooks(allBooks);
            setBooks(grouped);
        } catch (error) {
            console.error(error);
        }
    };

    const groupBooks = (booksList) => {
        const map = new Map();
        booksList.forEach(book => {
            const authorNames = (book.authors || []).map(a => a.name).sort().join(",");
            const categoryNames = (book.categories || []).map(c => c.name).sort().join(",");
            const key = `${book.title}|${book.publicationYear}|${authorNames}|${categoryNames}`;

            if (map.has(key)) {
                const existing = map.get(key);
                existing.count++;
                existing.ids.push(book.id);
            } else {
                map.set(key, { ...book, count: 1, ids: [book.id] });
            }
        });
        return Array.from(map.values());
    };

    const filteredBooks = (books || []).filter((book) => {
        const authorNames = (book.authors || []).map((a) => a.name.toLowerCase());
        const categoryNames = (book.categories || []).map((c) => c.name.toLowerCase());

        const matchesTitle = !searchTitle || book.title?.toLowerCase().includes(searchTitle.toLowerCase());
        const matchesAuthor = !searchAuthor || authorNames.some((name) => name.includes(searchAuthor.toLowerCase()));
        const matchesCategory = !searchCategory || categoryNames.some((name) => name.includes(searchCategory.toLowerCase()));
        const matchesYearFrom = !yearFrom || book.publicationYear >= Number(yearFrom);
        const matchesYearTo = !yearTo || book.publicationYear <= Number(yearTo);
        const matchesUrlAuthor = !authorFilter || authorNames.some((name) => name.includes(authorFilter.toLowerCase()));
        const matchesUrlCategory = !categoryFilter || categoryNames.some((name) => name.includes(categoryFilter.toLowerCase()));

        return matchesTitle && matchesAuthor && matchesCategory && matchesYearFrom && matchesYearTo && matchesUrlAuthor && matchesUrlCategory;
    });

    const openBookDetails = async (book) => {
        setSelectedBook(book);
        setBookStats(null);

        try {
            const response = await axios.get(`${API_URL}/books/${book.id}/stats`);
            setBookStats(response.data);
        } catch (error) {
            console.error("Failed to load book stats", error);
            setBookStats({ totalCopies: 1, borrowedCopies: 0, availableCopies: 1 });
        }
    };

    const borrowBook = async (bookId) => {
        if (!isLoggedIn) {
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
            // Обновить статистику
            if (selectedBook) openBookDetails(selectedBook);
        } catch (error) {
            console.error(error);
            toast.error(error.response?.data?.message || "Cannot borrow book");
        }
    };

    const closeModal = () => {
        setSelectedBook(null);
        setBookStats(null);
        if (pdfUrl) {
            URL.revokeObjectURL(pdfUrl);
            setPdfUrl(null);
        }
    };

    const openPdf = async (bookId) => {
        try {
            const blob = await axios
                .get(`${API_URL}/books/${bookId}/pdf`, { responseType: "blob" })
                .then((res) => res.data);
            const url = window.URL.createObjectURL(blob);
            window.open(url, "_blank");
        } catch (error) {
            console.error(error);
            toast.error("Failed to open PDF");
        }
    };

    const downloadPdf = async (bookId, title) => {
        try {
            const blob = await axios
                .get(`${API_URL}/books/${bookId}/pdf`, { responseType: "blob" })
                .then((res) => res.data);
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `${title}.pdf`;
            a.click();
            URL.revokeObjectURL(url);
        } catch (error) {
            console.error(error);
            toast.error("Failed to download PDF");
        }
    };

    return (
        <div className="page">
            <h1>Books</h1>

            <div className="search-panel">
                <input placeholder="Title..." value={searchTitle} onChange={(e) => setSearchTitle(e.target.value)} />
                <input placeholder="Author..." value={searchAuthor} onChange={(e) => setSearchAuthor(e.target.value)} />
                <input placeholder="Category..." value={searchCategory} onChange={(e) => setSearchCategory(e.target.value)} />
                <input type="number" placeholder="Year from..." value={yearFrom} onChange={(e) => setYearFrom(e.target.value)} />
                <input type="number" placeholder="Year to..." value={yearTo} onChange={(e) => setYearTo(e.target.value)} />
            </div>

            <div className="books-grid">
                {filteredBooks.map((book) => (
                    <div
                        key={book.id}
                        className="book-card"
                        onClick={() => openBookDetails(book)}
                    >
                        <h2>{book.title}</h2>
                        <p><strong>Year:</strong> {book.publicationYear}</p>
                        <p><strong>Authors:</strong> {(book.authors || []).map((a) => a.name).join(", ")}</p>
                        <p><strong>Categories:</strong> {(book.categories || []).map((c) => c.name).join(", ")}</p>
                        {book.count > 1 && (
                            <p className="copy-count">📚 {book.count} copies</p>
                        )}
                    </div>
                ))}
            </div>

            {selectedBook && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>{selectedBook.title}</h2>
                            <button className="close-btn" onClick={closeModal}>×</button>
                        </div>

                        <div className="modal-body">
                            <p><strong>Year:</strong> {selectedBook.publicationYear}</p>
                            <p><strong>Authors:</strong> {(selectedBook.authors || []).map((a) => a.name).join(", ")}</p>
                            <p><strong>Categories:</strong> {(selectedBook.categories || []).map((c) => c.name).join(", ")}</p>

                            {bookStats && (
                                <div className="book-stats">
                                    <p>📚 <strong>Total copies:</strong> {bookStats.totalCopies}</p>
                                    <p>📖 <strong>Borrowed:</strong> {bookStats.borrowedCopies}</p>
                                    <p>✅ <strong>Available:</strong> {bookStats.availableCopies}</p>
                                </div>
                            )}

                            <div className="modal-actions">
                                <button
                                    className={`borrow-btn ${!isLoggedIn || bookStats?.availableCopies === 0 ? 'disabled-btn' : ''}`}
                                    onClick={() => borrowBook(selectedBook.id)}
                                    disabled={!isLoggedIn || bookStats?.availableCopies === 0}
                                    title={!isLoggedIn ? "Login first" : bookStats?.availableCopies === 0 ? "No copies available" : ""}
                                >
                                    {!isLoggedIn ? "🔒 Borrow" : bookStats?.availableCopies === 0 ? "Unavailable" : "Borrow"}
                                </button>
                                <button className="borrow-btn" onClick={() => openPdf(selectedBook.id)}>View PDF</button>
                                <button className="borrow-btn" onClick={() => downloadPdf(selectedBook.id, selectedBook.title)}>Download PDF</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default BooksPage;