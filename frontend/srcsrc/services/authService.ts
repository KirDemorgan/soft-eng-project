import axios from 'axios';

const API_URL = '/api/users';

export const authService = {
  login: async (username, password) => {
    const response = await axios.post(`${API_URL}/login`, { username, password });
    return response.data;
  },
  register: async (username, email, password) => {
    const response = await axios.post(`${API_URL}/register`, { username, email, password });
    return response.data;
  },
};
