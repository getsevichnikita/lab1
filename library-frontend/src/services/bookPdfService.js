import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export const getBookPdf = async (bookId, mode = "inline") => {
    const res = await axios.get(
        `${API_URL}/books/${bookId}/pdf`,
        {
            params: { mode },
            responseType: "blob"
        }
    );

    return res.data;
};