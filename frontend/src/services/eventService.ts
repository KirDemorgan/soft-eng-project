import axios from 'axios';

const API_URL = '/api/events';

export const eventService = {
  getActiveEvents: async () => {
    const response = await axios.get(`${API_URL}/active`);
    return response.data;
  },
};
