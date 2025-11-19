import create from 'zustand';

interface UserState {
  userId: number | null;
  setUserId: (userId: number | null) => void;
}

export const useUserStore = create<UserState>((set) => ({
  userId: null,
  setUserId: (userId) => set({ userId }),
}));
