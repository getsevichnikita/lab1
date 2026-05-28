import axios from "axios";

const API_URL = "http://localhost:8080";

export const getBooks = async () => {
    const response = await axios.get(`${API_URL}/books?page=0&size=50`);
    return response.data;
};

export const searchBooksByAuthor = async (
    authorName
) => {

    const response = await axios.get(
        `${API_URL}/books/search/jpql`,
        {
            params: {
                author: authorName,
                page: 0,
                size: 50
            }
        }
    );

    return response.data;
};
