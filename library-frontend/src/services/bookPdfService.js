import axios from "axios";

const API_URL = "https://library-api-v8wu.onrender.com";

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