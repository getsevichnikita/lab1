import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";

const API_URL = "https://library-api-v8wu.onrender.com";

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
  const [covers, setCovers] = useState({});
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

  useEffect(() => {
    myBooks.forEach(book => loadCover(book.id));
  }, [myBooks]);

  const resetSelection = () => {
    setSelectedLoan(null);
    setSelectedBook(null);
    setEditMode(false);
    setNewPdfFile(null);
  };

  const loadCover = async (bookId) => {
    if (covers[bookId] !== undefined) return;
    try {
      const response = await axios.get(`${API_URL}/books/${bookId}/cover`, { responseType: "blob" });
      const url = URL.createObjectURL(response.data);
      setCovers(prev => ({ ...prev, [bookId]: url }));
    } catch (error) {
      setCovers(prev => ({ ...prev, [bookId]: null }));
    }
  };

  const loadData = async () => {
    try {
      const [loansRes, booksRes] = await Promise.all([
        axios.get(`${API_URL}/loans/reader?readerId=${readerId}&page=0&size=100`),
        axios.get(`${API_URL}/books?page=0&size=100`),
      ]);
      const allBooks = Array.isArray(booksRes.data) ? booksRes.data : (booksRes.data.content || []);
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
      toast.error("Не удалось загрузить данные");
    }
  };

  const getBookById = (id) => books.find(b => b.id === id);
  const getBookTitle = (id) => {
    const book = books.find(b => b.id === id);
    return book ? book.title : "Неизвестно";
  };

  const myLoans = loans.filter(loan => loan?.readerId != null && Number(loan.readerId) === Number(readerId));

  const cancelLoan = async (loanId) => {
    try {
      await axios.delete(`${API_URL}/loans/${loanId}`);
      setLoans(prev => prev.filter(l => l.id !== loanId));
      resetSelection();
      toast.success("Заказ отменён");
    } catch (error) {
      console.error(error);
      toast.error("Не удалось отменить заказ");
    }
  };

  const deleteBook = async (id) => {
    try {
      await axios.delete(`${API_URL}/books/${id}`);
      setBooks(prev => prev.filter(b => b.id !== id));
      setMyBooks(prev => prev.filter(b => b.id !== id));
      resetSelection();
      toast.success("Книга удалена");
    } catch (error) {
      console.error(error);
      toast.error("Не удалось удалить книгу");
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
      toast.warn("Автор уже в списке");
      return;
    }
    setEditAuthors(prev => [...prev, { id: null, name: trimmed }]);
    setNewAuthorName("");
  };

  const removeAuthor = (index) => setEditAuthors(prev => prev.filter((_, i) => i !== index));
  const addCategory = () => {
    const trimmed = newCategoryName.trim();
    if (!trimmed) return;
    if (editCategories.some(c => c.name.toLowerCase() === trimmed.toLowerCase())) {
      toast.warn("Жанр уже в списке");
      return;
    }
    setEditCategories(prev => [...prev, { id: null, name: trimmed }]);
    setNewCategoryName("");
  };

  const removeCategory = (index) => setEditCategories(prev => prev.filter((_, i) => i !== index));

  const ensureEntityExists = async (type, name) => {
    try {
      const endpoint = type === "author" ? "authors" : "categories";
      const res = await axios.post(`${API_URL}/${endpoint}`, { name });
      return res.data.id;
    } catch (error) {
      console.error(`Не удалось создать ${type}: ${name}`, error);
      throw error;
    }
  };

  const updateBook = async () => {
    try {
      const authorIds = await Promise.all(editAuthors.map(async (a) => {
        if (a.id) return a.id;
        return await ensureEntityExists("author", a.name);
      }));
      const categoryIds = await Promise.all(editCategories.map(async (c) => {
        if (c.id) return c.id;
        return await ensureEntityExists("category", c.name);
      }));

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
          toast.success("PDF обновлён");
        } catch (pdfError) {
          console.error(pdfError);
          toast.error("Книга сохранена, но не удалось обновить PDF");
        }
      }

      const updatedBook = { ...selectedBook, title: editTitle, publicationYear: Number(editYear),
        authors: editAuthors.map((a, idx) => ({ id: authorIds[idx], name: a.name })),
        categories: editCategories.map((c, idx) => ({ id: categoryIds[idx], name: c.name })),
      };

      setBooks(prev => prev.map(b => (b.id === selectedBook.id ? updatedBook : b)));
      setMyBooks(prev => prev.map(b => (b.id === selectedBook.id ? updatedBook : b)));
      setSelectedBook(updatedBook);
      setEditMode(false);
      setNewPdfFile(null);
      toast.success("Книга обновлена");
    } catch (error) {
      console.error(error);
      toast.error("Не удалось обновить книгу");
    }
  };

  const openPdf = async (bookId, download = false) => {
    try {
      const res = await axios.get(`${API_URL}/books/${bookId}/pdf`, { responseType: "blob" });
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
      toast.error("Не удалось открыть PDF");
    }
  };

  return (
    <div className="page">
      <div className="profile-header">
        <h1>Мой профиль — {readerName}</h1>
        <button className="logout-btn" onClick={handleLogout}>Выйти</button>
      </div>

      <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
        <button className="borrow-btn" onClick={() => { setActiveTab("loans"); resetSelection(); }}>Мои заказы</button>
        <button className="borrow-btn" onClick={() => { setActiveTab("books"); resetSelection(); }}>Мои книги</button>
      </div>

      {activeTab === "loans" && (
        <>
          <h2>Мои заказы</h2>
          <div className="books-grid">
            {myLoans.map(loan => (
              <div key={loan.id} className="book-card" onClick={() => { setSelectedLoan(loan); setSelectedBook(getBookById(loan.bookId)); setEditMode(false); }}>
                <div className="book-cover">
                  {covers[loan.bookId] ? (
                    <img src={covers[loan.bookId]} alt={getBookTitle(loan.bookId)} />
                  ) : covers[loan.bookId] === null ? (
                    <div className="no-cover">Без обложки</div>
                  ) : (
                    <div className="loading-cover">Загрузка...</div>
                  )}
                </div>
                <div className="book-info">
                  <h3>{getBookTitle(loan.bookId)}</h3>
                  <p>Выдана: {loan.issueDate}</p>
                  <p>Возврат: {loan.returnDate || "не возвращена"}</p>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      {activeTab === "books" && (
        <>
          <h2>Мои книги</h2>
          <div className="books-grid">
            {myBooks.map(book => (
              <div key={book.id} className="book-card" onClick={() => { setSelectedBook(book); setSelectedLoan(null); setEditMode(false); }}>
                <div className="book-cover">
                  {covers[book.id] ? (
                    <img src={covers[book.id]} alt={book.title} />
                  ) : covers[book.id] === null ? (
                    <div className="no-cover">Без обложки</div>
                  ) : (
                    <div className="loading-cover">Загрузка...</div>
                  )}
                </div>
                <div className="book-info">
                  <h3>{book.title}</h3>
                  <p>Год: {book.publicationYear}</p>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      {selectedBook && (
        <div className="modal-overlay" onClick={resetSelection}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editMode ? "Редактирование" : selectedBook.title}</h2>
              <button className="close-btn" onClick={resetSelection}>x</button>
            </div>
            <div className="modal-body">
              {editMode ? (
                <div className="edit-form">
                  <label>Название:</label>
                  <input value={editTitle} onChange={(e) => setEditTitle(e.target.value)} />
                  <label>Год:</label>
                  <input type="number" value={editYear} onChange={(e) => setEditYear(e.target.value)} />
                  <div className="form-section">
                    <label><strong>Авторы:</strong></label>
                    <div className="tags-container">
                      {editAuthors.map((author, idx) => (
                        <span key={idx} className="tag">{author.name}
                          <button type="button" onClick={() => removeAuthor(idx)} className="tag-remove">x</button>
                        </span>
                      ))}
                    </div>
                    <input value={newAuthorName} onChange={(e) => setNewAuthorName(e.target.value)}
                      onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addAuthor(); } }}
                      placeholder="Новый автор" />
                  </div>
                  <div className="form-section">
                    <label><strong>Жанры:</strong></label>
                    <div className="tags-container">
                      {editCategories.map((cat, idx) => (
                        <span key={idx} className="tag">{cat.name}
                          <button type="button" onClick={() => removeCategory(idx)} className="tag-remove">x</button>
                        </span>
                      ))}
                    </div>
                    <input value={newCategoryName} onChange={(e) => setNewCategoryName(e.target.value)}
                      onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addCategory(); } }}
                      placeholder="Новый жанр" />
                  </div>
                  <div className="form-section">
                    <label><strong>Заменить PDF:</strong></label>
                    <input type="file" accept=".pdf" ref={fileInputRef} onChange={(e) => setNewPdfFile(e.target.files[0])} />
                  </div>
                </div>
              ) : (
                <>
                  <p><b>Название:</b> {selectedBook.title}</p>
                  <p><b>Год:</b> {selectedBook.publicationYear}</p>
                  <p><b>Авторы:</b> {(selectedBook.authors || []).map(a => a.name).join(", ")}</p>
                  <p><b>Жанры:</b> {(selectedBook.categories || []).map(c => c.name).join(", ")}</p>
                  {selectedLoan && (
                    <>
                      <p><b>Выдана:</b> {selectedLoan.issueDate}</p>
                      <p><b>Возврат:</b> {selectedLoan.returnDate || "не возвращена"}</p>
                    </>
                  )}
                </>
              )}
            </div>
            <div className="modal-actions">
              {selectedLoan && !editMode && (
                <button className="borrow-btn" onClick={() => cancelLoan(selectedLoan.id)}>Отменить заказ</button>
              )}
              {!editMode && (
                <>
                  <button className="borrow-btn" onClick={() => openPdf(selectedBook.id, false)}>Читать</button>
                  <button className="borrow-btn" onClick={() => openPdf(selectedBook.id, true)}>Скачать</button>
                  {activeTab === "books" && (
                    <>
                      <button className="borrow-btn" onClick={startEdit}>Редактировать</button>
                      <button className="borrow-btn" onClick={() => deleteBook(selectedBook.id)}>Удалить</button>
                    </>
                  )}
                </>
              )}
              {editMode && (
                <>
                  <button className="borrow-btn" onClick={updateBook}>Сохранить</button>
                  <button className="borrow-btn" onClick={() => setEditMode(false)}>Закрыть</button>
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