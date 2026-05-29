import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";;

export const getCategories = async () => {

    const response = await axios.get(
        `${API_URL}/categories?page=0&size=100`
    );

    return response.data;
};
