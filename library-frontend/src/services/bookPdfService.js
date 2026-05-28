import axios from "axios";

export const getBookPdf = async (bookId, mode = "inline") => {
    const res = await axios.get(
        `http://localhost:8080/books/${bookId}/pdf`,
        {
            params: { mode },
            responseType: "blob"
        }
    );

    return res.data;
};