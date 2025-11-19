import axios from 'axios';

const API_URL = '/api/bets';

export const bettingService = {
  placeBet: async (bet) => {
    const response = await axios.post(API_URL, bet);
    return response.data;
  },
  getUserBets: async (userId) => {
    const response = await axios.get(`${API_URL}/user/${userId}`);
    return response.data;
  },
};
