import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";
import PropTypes from 'prop-types';

const API_URL = "https://library-api-v8wu.onrender.com";
ProfilePage.propTypes = {
  readerId: PropTypes.number,
  readerName: PropTypes.string,
  onLogout: PropTypes.func.isRequired,
};
function ProfilePage({ readerId, readerName, onLogout }) {
  const [activeTab, setActiveTab] = useState("loans");

  const [loans, setLoans] = useState([]);
  const [books, setBooks] = useState([]);
  const [myBooks, setMyBooks] = useState([]);

  const [selectedLoan, setSelectedLoan] = useState(null);
  const [selectedBook, setSelectedBook] = useState(null);

  const [editMode, setEditMode] = useState(false);

  const [editTitle, setEditTitle] = useState("");
  const [editYear, setEditYear] = useState("");
  const [editAuthors, setEditAuthors] = useState([]);
  const [editCategories, setEditCategories] = useState([]);
  const [newAuthorName, setNewAuthorName] = useState("");
  const [newCategoryName, setNewCategoryName] = useState("");

  const [newPdfFile, setNewPdfFile] = useState(null);
  const fileInputRef = useRef(null);

  const navigate = useNavigate();

  const handleLogout = () => {
    onLogout();
    navigate("/login");
  };

  useEffect(() => {
    if (!readerId) return;
    loadData();
  }, [readerId]);

  const resetSelection = () => {
    setSelectedLoan(null);
    setSelectedBook(null);
    setEditMode(false);
    setNewPdfFile(null);
  };

  const loadData = async () => {
    try {
      const [loansRes, booksRes] = await Promise.all([
        axios.get(`${API_URL}/loans/reader?readerId=${readerId}&page=0&size=100`),
        axios.get(`${API_URL}/books?page=0&size=100`),
      ]);

      const allBooks = Array.isArray(booksRes.data)
        ? booksRes.data
        : (booksRes.data.content || []);

      setLoans(loansRes.data || []);
      setBooks(allBooks);

      const currentReaderId = readerId ? Number(readerId) : null;

      const my = allBooks.filter(b => {
        const bookOwnerId = b.ownerId != null ? Number(b.ownerId) : null;
        return bookOwnerId !== null && bookOwnerId === currentReaderId;
      });

      setMyBooks(my);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load data");
    }
  };

  const getBookById = (id) => books.find(b => b.id === id);
  const getBookTitle = (id) => {
    const book = books.find(b => b.id === id);
    return book ? book.title : "Unknown";
  };

  const myLoans = loans.filter(
    loan => loan?.readerId != null && Number(loan.readerId) === Number(readerId)
  );

  const cancelLoan = async (loanId) => {
    try {
      await axios.delete(`${API_URL}/loans/${loanId}`);
      setLoans(prev => prev.filter(l => l.id !== loanId));
      resetSelection();
      toast.success("Loan cancelled");
    } catch (error) {
      console.error(error);
      toast.error("Cancel failed");
    }
  };

  const deleteBook = async (id) => {
    try {
      await axios.delete(`${API_URL}/books/${id}`);
      setBooks(prev => prev.filter(b => b.id !== id));
      setMyBooks(prev => prev.filter(b => b.id !== id));
      resetSelection();
      toast.success("Book deleted");
    } catch (error) {
      console.error(error);
      toast.error("Delete failed");
    }
  };

  const startEdit = () => {
    setEditMode(true);
    setEditTitle(selectedBook.title);
    setEditYear(selectedBook.publicationYear);
    setEditAuthors(selectedBook.authors ? selectedBook.authors.map(a => ({ ...a })) : []);
    setEditCategories(selectedBook.categories ? selectedBook.categories.map(c => ({ ...c })) : []);
    setNewAuthorName("");
    setNewCategoryName("");
    setNewPdfFile(null);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const addAuthor = () => {
    const trimmed = newAuthorName.trim();
    if (!trimmed) return;
    if (editAuthors.some(a => a.name.toLowerCase() === trimmed.toLowerCase())) {
      toast.warn("Author already in the list");
      return;
    }
    setEditAuthors(prev => [...prev, { id: null, name: trimmed }]);
    setNewAuthorName("");
  };

  const removeAuthor = (index) => {
    setEditAuthors(prev => prev.filter((_, i) => i !== index));
  };

  const addCategory = () => {
    const trimmed = newCategoryName.trim();
    if (!trimmed) return;
    if (editCategories.some(c => c.name.toLowerCase() === trimmed.toLowerCase())) {
      toast.warn("Category already in the list");
      return;
    }
    setEditCategories(prev => [...prev, { id: null, name: trimmed }]);
    setNewCategoryName("");
  };

  const removeCategory = (index) => {
    setEditCategories(prev => prev.filter((_, i) => i !== index));
  };

  const ensureEntityExists = async (type, name) => {
    try {
      const endpoint = type === "author" ? "authors" : "categories";
      const res = await axios.post(`${API_URL}/${endpoint}`, { name });
      return res.data.id;
    } catch (error) {
      console.error(`Failed to create ${type}: ${name}`, error);
      throw error;
    }
  };

  const updateBook = async () => {
    try {
      const authorIds = await Promise.all(
        editAuthors.map(async (a) => {
          if (a.id) return a.id;
          const newId = await ensureEntityExists("author", a.name);
          return newId;
        })
      );
      const categoryIds = await Promise.all(
        editCategories.map(async (c) => {
          if (c.id) return c.id;
          const newId = await ensureEntityExists("category", c.name);
          return newId;
        })
      );

      await axios.put(`${API_URL}/books/${selectedBook.id}`, {
        title: editTitle,
        publicationYear: Number(editYear),
        authorIds,
        categoryIds,
      });

      if (newPdfFile) {
        const formData = new FormData();
        formData.append("file", newPdfFile);
        try {
          await axios.put(`${API_URL}/books/${selectedBook.id}/pdf`, formData);
          toast.success("PDF updated");
        } catch (pdfError) {
          console.error(pdfError);
          toast.error("Book saved, but PDF update failed");
        }
      }

      const updatedBook = {
        ...selectedBook,
        title: editTitle,
        publicationYear: Number(editYear),
        authors: editAuthors.map((a, idx) => ({ id: authorIds[idx], name: a.name })),
        categories: editCategories.map((c, idx) => ({ id: categoryIds[idx], name: c.name })),
      };

      setBooks(prev => prev.map(b => (b.id === selectedBook.id ? updatedBook : b)));
      setMyBooks(prev => prev.map(b => (b.id === selectedBook.id ? updatedBook : b)));
      setSelectedBook(updatedBook);
      setEditMode(false);
      setNewPdfFile(null);
      toast.success("Book updated");
    } catch (error) {
      console.error(error);
      toast.error("Update failed");
    }
  };

  const openPdf = async (bookId, download = false) => {
    try {
      const res = await axios.get(`${API_URL}/books/${bookId}/pdf`, {
        responseType: "blob",
      });
    const url = globalThis.URL.createObjectURL(res.data);
      if (download) {
        const a = document.createElement("a");
        a.href = url;
        a.download = "book.pdf";
        a.click();
      } else {
        window.open(url, "_blank");
      }
    } catch (error) {
      console.error(error);
      toast.error("Failed to open PDF");
    }
  };

  return (
    <div className="page">
      <div className="profile-header">
        <h1>My Profile — {readerName}</h1>
        <button className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </div>

      <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
        <button className="borrow-btn" onClick={() => { setActiveTab("loans"); resetSelection(); }}>
          My Loans
        </button>
        <button className="borrow-btn" onClick={() => { setActiveTab("books"); resetSelection(); }}>
          My Books
        </button>
      </div>

      {activeTab === "loans" && (
        <>
          <h2>My Loans</h2>
          <div className="books-grid">
            {myLoans.map(loan => (
              <div
                key={loan.id}
                className="book-card"
                onClick={() => {
                  setSelectedLoan(loan);
                  setSelectedBook(getBookById(loan.bookId));
                  setEditMode(false);
                }}
              >
                <h3>{getBookTitle(loan.bookId)}</h3>
                <p>Issue: {loan.issueDate}</p>
                <p>Return: {loan.returnDate || "not returned"}</p>
              </div>
            ))}
          </div>
        </>
      )}

      {activeTab === "books" && (
        <>
          <h2>My Books</h2>
          <div className="books-grid">
            {myBooks.map(book => (
              <div
                key={book.id}
                className="book-card"
                onClick={() => {
                  setSelectedBook(book);
                  setSelectedLoan(null);
                  setEditMode(false);
                }}
              >
                <h3>{book.title}</h3>
                <p>Year: {book.publicationYear}</p>
              </div>
            ))}
          </div>
        </>
      )}

      {selectedBook && (
        <div className="modal-overlay" onClick={resetSelection}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editMode ? "Edit Book" : selectedBook.title}</h2>
              <button className="close-btn" onClick={resetSelection}>
                ×
              </button>
            </div>

            <div className="modal-body">
              {editMode ? (
                <div className="edit-form">
                  <label>Title:</label>
                  <input value={editTitle} onChange={(e) => setEditTitle(e.target.value)} />
                  <label>Year:</label>
                  <input type="number" value={editYear} onChange={(e) => setEditYear(e.target.value)} />

                  <div className="form-section">
                    <label><strong>Authors:</strong></label>
                    <div className="tags-container">
                      {editAuthors.map((author, idx) => (
                        <span key={idx} className="tag">
                          {author.name}
                          <button
                            type="button"
                            onClick={() => removeAuthor(idx)}
                            className="tag-remove"
                            aria-label="Remove author"
                          >
                            ×
                          </button>
                        </span>
                      ))}
                    </div>
                    <input
                      value={newAuthorName}
                      onChange={(e) => setNewAuthorName(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          addAuthor();
                        }
                      }}
                      placeholder="New author name"
                    />
                  </div>

                  <div className="form-section">
                    <label><strong>Categories:</strong></label>
                    <div className="tags-container">
                      {editCategories.map((cat, idx) => (
                        <span key={idx} className="tag">
                          {cat.name}
                          <button
                            type="button"
                            onClick={() => removeCategory(idx)}
                            className="tag-remove"
                            aria-label="Remove category"
                          >
                            ×
                          </button>
                        </span>
                      ))}
                    </div>
                    <input
                      value={newCategoryName}
                      onChange={(e) => setNewCategoryName(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          addCategory();
                        }
                      }}
                      placeholder="New category name"
                    />
                  </div>

                  <div className="form-section">
                    <label><strong>Replace PDF:</strong></label>
                    <input
                      type="file"
                      accept=".pdf"
                      ref={fileInputRef}
                      onChange={(e) => setNewPdfFile(e.target.files[0])}
                    />
                  </div>
                </div>
              ) : (
                <>
                  <p><b>Title:</b> {selectedBook.title}</p>
                  <p><b>Year:</b> {selectedBook.publicationYear}</p>
                  <p><b>Authors:</b> {(selectedBook.authors || []).map(a => a.name).join(", ")}</p>
                  <p><b>Categories:</b> {(selectedBook.categories || []).map(c => c.name).join(", ")}</p>
                  {selectedLoan && (
                    <>
                      <p><b>Issue:</b> {selectedLoan.issueDate}</p>
                      <p><b>Return:</b> {selectedLoan.returnDate || "not returned"}</p>
                    </>
                  )}
                </>
              )}
            </div>

            <div className="modal-actions">
              {selectedLoan && !editMode && (
                <button className="borrow-btn" onClick={() => cancelLoan(selectedLoan.id)}>
                  Cancel Loan
                </button>
              )}

              {!editMode && (
                <>
                  <button className="borrow-btn" onClick={() => openPdf(selectedBook.id, false)}>
                    View PDF
                  </button>
                  <button className="borrow-btn" onClick={() => openPdf(selectedBook.id, true)}>
                    Download
                  </button>
                  {activeTab === "books" && (
                    <>
                      <button className="borrow-btn" onClick={startEdit}>
                        Edit
                      </button>
                      <button className="borrow-btn" onClick={() => deleteBook(selectedBook.id)}>
                        Delete
                      </button>
                    </>
                  )}
                </>
              )}

              {editMode && (
                <>
                  <button className="borrow-btn" onClick={updateBook}>
                    Save
                  </button>
                  <button className="borrow-btn" onClick={() => setEditMode(false)}>
                    Close
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ProfilePage;