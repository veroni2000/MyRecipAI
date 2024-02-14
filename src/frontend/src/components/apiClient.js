import axios from "axios";

const API_URL = "/api";

const axiosInstance = axios.create({
    baseURL: API_URL,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

axiosInstance.interceptors.request.use(
    async (config) => {
        // Wait for the token to be retrieved from local storage
        const token = await localStorage.getItem("jwtToken");

        // Set the Authorization header if the token is available
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosInstance;
