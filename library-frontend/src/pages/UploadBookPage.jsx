import { useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

function UploadBookPage() {
  const [title, setTitle] = useState("");
  const [publicationYear, setPublicationYear] = useState("");
  const [authorInput, setAuthorInput] = useState("");
  const [categoryInput, setCategoryInput] = useState("");

  const [selectedAuthors, setSelectedAuthors] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);

  const [pdfFile, setPdfFile] = useState(null);

  const readerId = localStorage.getItem("readerId");

  const addAuthor = () => {
    if (!authorInput.trim()) return;
    setSelectedAuthors(prev => [
      ...prev,
      { id: Date.now(), name: authorInput.trim() }
    ]);
    setAuthorInput("");
  };

  const addCategory = () => {
    if (!categoryInput.trim()) return;
    setSelectedCategories(prev => [
      ...prev,
      { id: Date.now(), name: categoryInput.trim() }
    ]);
    setCategoryInput("");
  };

  const removeAuthor = (id) => {
    setSelectedAuthors(prev => prev.filter(a => a.id !== id));
  };

  const removeCategory = (id) => {
    setSelectedCategories(prev => prev.filter(c => c.id !== id));
  };

  const uploadBook = async () => {
    if (!readerId) {
      toast.info("Login first");
      return;
    }

    if (!title.trim()) {
      toast.error("Enter title");
      return;
    }
    if (!publicationYear) {
      toast.error("Enter publication year");
      return;
    }
    if (selectedAuthors.length === 0) {
      toast.error("Add at least one author");
      return;
    }
    if (selectedCategories.length === 0) {
      toast.error("Add at least one category");
      return;
    }
    if (!pdfFile) {
      toast.error("Choose PDF file");
      return;
    }

    try {
      const bookDto = {
        title,
        publicationYear: Number(publicationYear),
        authors: selectedAuthors.map(a => ({ name: a.name })),
        categories: selectedCategories.map(c => ({ name: c.name })),
        ownerId: Number(readerId),
      };

      const formData = new FormData();
      formData.append(
        "book",
        new Blob([JSON.stringify(bookDto)], { type: "application/json" })
      );
      formData.append("file", pdfFile);

      await axios.post(`${API_URL}/books/upload`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      toast.success("Book uploaded");

      // Очистка формы после успешной загрузки
      setTitle("");
      setPublicationYear("");
      setSelectedAuthors([]);
      setSelectedCategories([]);
      setPdfFile(null);

    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Upload failed");
    }
  };

  return (
    <div className="page">
      <h1>Upload Book</h1>

      <div className="search-panel-vertical">
        <input
          placeholder="Title..."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <input
          type="number"
          placeholder="Publication year..."
          value={publicationYear}
          onChange={(e) => setPublicationYear(e.target.value)}
        />

        <input
          placeholder="Add author (Enter)..."
          value={authorInput}
          onChange={(e) => setAuthorInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              addAuthor();
            }
          }}
        />
        <div className="tags-container">
          {selectedAuthors.map(a => (
            <span key={a.id} className="tag">
              {a.name}
              <button
                type="button"
                className="tag-remove"
                onClick={() => removeAuthor(a.id)}
                aria-label="Remove author"
              >
                ×
              </button>
            </span>
          ))}
        </div>

        <input
          placeholder="Add category (Enter)..."
          value={categoryInput}
          onChange={(e) => setCategoryInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              addCategory();
            }
          }}
        />
        <div className="tags-container">
          {selectedCategories.map(c => (
            <span key={c.id} className="tag">
              {c.name}
              <button
                type="button"
                className="tag-remove"
                onClick={() => removeCategory(c.id)}
                aria-label="Remove category"
              >
                ×
              </button>
            </span>
          ))}
        </div>

        <input
          type="file"
          accept="application/pdf"
          onChange={(e) => setPdfFile(e.target.files[0])}
        />

        <button className="borrow-btn" onClick={uploadBook}>
          Upload
        </button>
      </div>
    </div>
  );
}

export default UploadBookPage;