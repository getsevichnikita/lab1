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
      toast.warn("Author already in the list");
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
      toast.warn("Category already in the list");
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
      toast.info("Login first");
      return;
    }

    // Добавляем автора и категорию из поля ввода, если есть
    if (authorInput.trim()) addAuthorFromInput(authorInput.trim());
    if (categoryInput.trim()) addCategoryFromInput(categoryInput.trim());

    // Даём React обновить состояние
    await new Promise(resolve => setTimeout(resolve, 50));

    if (!title.trim()) {
      toast.error("Enter title");
      return;
    }
    if (!publicationYear) {
      toast.error("Enter publication year");
      return;
    }
    if (selectedAuthors.length === 0 && !authorInput.trim()) {
      toast.error("Add at least one author");
      return;
    }
    if (selectedCategories.length === 0 && !categoryInput.trim()) {
      toast.error("Add at least one category");
      return;
    }
    if (!pdfFile) {
      toast.error("Choose PDF file");
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

      toast.success("Book uploaded");
      setTitle("");
      setPublicationYear("");
      setSelectedAuthors([]);
      setSelectedCategories([]);
      setAuthorInput("");
      setCategoryInput("");
      setPdfFile(null);

    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Upload failed");
    }
  };

  return (
    <div className="page">
      <h1>Upload Book</h1>

      <div className={`search-panel-vertical ${!isLoggedIn ? 'disabled-panel' : ''}`}>
        <input
          placeholder="Title..."
          value={title}
          onChange={(e) => isLoggedIn && setTitle(e.target.value)}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "You need to login first" : ""}
        />

        <input
          type="number"
          placeholder="Publication year..."
          value={publicationYear}
          onChange={(e) => isLoggedIn && setPublicationYear(e.target.value)}
          disabled={!isLoggedIn}
          title={!isLoggedIn ? "You need to login first" : ""}
        />

        <input
          placeholder="Add author (comma or Enter to add)..."
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
          title={!isLoggedIn ? "You need to login first" : ""}
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
          placeholder="Add category (comma or Enter to add)..."
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
          title={!isLoggedIn ? "You need to login first" : ""}
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
          title={!isLoggedIn ? "You need to login first" : ""}
        />

     <button
       className={`borrow-btn ${!isLoggedIn ? 'disabled-btn' : ''}`}
       onClick={uploadBook}
       disabled={!isLoggedIn}
       title={!isLoggedIn ? "You need to login first" : "Upload book to library"}
     >
       Upload
     </button>
      </div>
    </div>
  );
}

export default UploadBookPage;