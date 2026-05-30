import { useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";

const API_URL = "https://library-api-v8wu.onrender.com";

function UploadBookPage() {
  const readerId = localStorage.getItem("readerId");
  const isLoggedIn = !!readerId;

  const [title, setTitle] = useState("");
  const [publicationYear, setPublicationYear] = useState("");
  const [authorInput, setAuthorInput] = useState("");
  const [categoryInput, setCategoryInput] = useState("");
  const [selectedAuthors, setSelectedAuthors] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [pdfFile, setPdfFile] = useState(null);

  const addAuthorFromInput = (name) => {
    const trimmed = name.trim();
    if (!trimmed) return;
    if (selectedAuthors.some(a => a.name.toLowerCase() === trimmed.toLowerCase())) {
      toast.warn("Автор уже в списке");
      setAuthorInput("");
      return;
    }
    setSelectedAuthors(prev => [...prev, { id: Date.now(), name: trimmed }]);
    setAuthorInput("");
  };

  const addCategoryFromInput = (name) => {
    const trimmed = name.trim();
    if (!trimmed) return;
    if (selectedCategories.some(c => c.name.toLowerCase() === trimmed.toLowerCase())) {
      toast.warn("Жанр уже в списке");
      setCategoryInput("");
      return;
    }
    setSelectedCategories(prev => [...prev, { id: Date.now(), name: trimmed }]);
    setCategoryInput("");
  };

  const addAuthor = () => {
    if (!isLoggedIn) return;
    addAuthorFromInput(authorInput);
  };

  const addCategory = () => {
    if (!isLoggedIn) return;
    addCategoryFromInput(categoryInput);
  };

  const removeAuthor = (id) => {
    if (!isLoggedIn) return;
    setSelectedAuthors(prev => prev.filter(a => a.id !== id));
  };

  const removeCategory = (id) => {
    if (!isLoggedIn) return;
    setSelectedCategories(prev => prev.filter(c => c.id !== id));
  };

  const uploadBook = async () => {
    if (!isLoggedIn) {
      toast.info("Сначала войдите в систему");
      return;
    }

    if (authorInput.trim()) addAuthorFromInput(authorInput.trim());
    if (categoryInput.trim()) addCategoryFromInput(categoryInput.trim());

    await new Promise(resolve => setTimeout(resolve, 50));

    if (!title.trim()) {
      toast.error("Введите название");
      return;
    }
    if (!publicationYear) {
      toast.error("Введите год издания");
      return;
    }
    if (selectedAuthors.length === 0 && !authorInput.trim()) {
      toast.error("Добавьте хотя бы одного автора");
      return;
    }
    if (selectedCategories.length === 0 && !categoryInput.trim()) {
      toast.error("Добавьте хотя бы один жанр");
      return;
    }
    if (!pdfFile) {
      toast.error("Выберите PDF-файл");
      return;
    }

    try {
      const finalAuthors = authorInput.trim()
        ? [...selectedAuthors, { id: Date.now(), name: authorInput.trim() }]
        : selectedAuthors;
      const finalCategories = categoryInput.trim()
        ? [...selectedCategories, { id: Date.now(), name: categoryInput.trim() }]
        : selectedCategories;

      const bookDto = {
        title,
        publicationYear: Number(publicationYear),
        authors: finalAuthors.map(a => ({ name: a.name })),
        categories: finalCategories.map(c => ({ name: c.name })),
        ownerId: Number(readerId),
      };

      const formData = new FormData();
      formData.append("book", new Blob([JSON.stringify(bookDto)], { type: "application/json" }));
      formData.append("file", pdfFile);

      await axios.post(`${API_URL}/books/upload`, formData);

      toast.success("Книга опубликована");
      setTitle("");
      setPublicationYear("");
      setSelectedAuthors([]);
      setSelectedCategories([]);
      setAuthorInput("");
      setCategoryInput("");
      setPdfFile(null);

    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Не удалось опубликовать книгу");
    }
  };

  return (
    <div className="page">
      <h1>Опубликовать книгу</h1>

      <div className={`search-panel-vertical ${!isLoggedIn ? 'disabled-panel' : ''}`}>
        <input
          placeholder="Название..."
          value={title}
          onChange={(e) => isLoggedIn && setTitle(e.target.value)}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "Сначала войдите в систему" : ""}
        />

        <input
          type="number"
          placeholder="Год издания..."
          value={publicationYear}
          onChange={(e) => isLoggedIn && setPublicationYear(e.target.value)}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "Сначала войдите в систему" : ""}
        />

        <input
          placeholder="Добавить автора (запятая или Enter)..."
          value={authorInput}
          onChange={(e) => {
            if (!isLoggedIn) return;
            const value = e.target.value;
            if (value.endsWith(',')) {
              addAuthorFromInput(value.slice(0, -1));
              return;
            }
            setAuthorInput(value);
          }}
          onKeyDown={(e) => {
            if (!isLoggedIn) return;
            if (e.key === "Enter") {
              e.preventDefault();
              addAuthor();
            }
          }}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "Сначала войдите в систему" : ""}
        />
        <div className="tags-container">
          {selectedAuthors.map(a => (
            <span key={a.id} className={`tag ${!isLoggedIn ? 'disabled-tag' : ''}`}>
              {a.name}
              {isLoggedIn && (
                <button type="button" className="tag-remove" onClick={() => removeAuthor(a.id)}>x</button>
              )}
            </span>
          ))}
        </div>

        <input
          placeholder="Добавить жанр (запятая или Enter)..."
          value={categoryInput}
          onChange={(e) => {
            if (!isLoggedIn) return;
            const value = e.target.value;
            if (value.endsWith(',')) {
              addCategoryFromInput(value.slice(0, -1));
              return;
            }
            setCategoryInput(value);
          }}
          onKeyDown={(e) => {
            if (!isLoggedIn) return;
            if (e.key === "Enter") {
              e.preventDefault();
              addCategory();
            }
          }}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "Сначала войдите в систему" : ""}
        />
        <div className="tags-container">
          {selectedCategories.map(c => (
            <span key={c.id} className={`tag ${!isLoggedIn ? 'disabled-tag' : ''}`}>
              {c.name}
              {isLoggedIn && (
                <button type="button" className="tag-remove" onClick={() => removeCategory(c.id)}>x</button>
              )}
            </span>
          ))}
        </div>

        <input
          type="file"
          accept="application/pdf"
          onChange={(e) => isLoggedIn && setPdfFile(e.target.files[0])}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "Сначала войдите в систему" : ""}
        />

        <button
          className={`borrow-btn ${!isLoggedIn ? 'disabled-btn' : ''}`}
          onClick={uploadBook}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "Сначала войдите в систему" : "Опубликовать книгу"}
        >
          Опубликовать
        </button>
      </div>
    </div>
  );
}

export default UploadBookPage;