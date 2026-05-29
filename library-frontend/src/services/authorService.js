import axios from "axios";

const API_URL = "https://library-api-v8wu.onrender.com";

export const getAuthors = async () => {

    const response = await axios.get(
        `${API_URL}/authors?page=0&size=100`
    );

    return response.data;
};
