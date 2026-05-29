import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";;

export const getAuthors = async () => {

    const response = await axios.get(
        `${API_URL}/authors?page=0&size=100`
    );

    return response.data;
};
