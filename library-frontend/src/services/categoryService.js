import axios from "axios";

const API_URL = "/api";

export const getCategories = async () => {

    const response = await axios.get(
        `${API_URL}/categories?page=0&size=100`
    );

    return response.data;
};
