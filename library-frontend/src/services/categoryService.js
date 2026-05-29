import axios from "axios";

const API_URL = "https://library-api-v8wu.onrender.com";

export const getCategories = async () => {

    const response = await axios.get(
        `${API_URL}/categories?page=0&size=100`
    );

    return response.data;
};
