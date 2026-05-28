import axios from "axios";

const API_URL = "/api";

export const getAuthors = async () => {

    const response = await axios.get(
        `${API_URL}/authors?page=0&size=100`
    );

    return response.data;
};
