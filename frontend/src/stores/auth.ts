import { defineStore } from 'pinia';

const apiURL = 'http://localhost:3000/';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
  }),
  actions: {
    async login(username: string, password: string) {
      const res = await fetch(`/${apiURL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
        credentials: 'include', // this sends the cookie
      });

      if (!res.ok) throw new Error('Login failed');

      const data = await res.json();
      this.token = data.accessToken;
      this.user = data.user;
    },

    logout() {
      this.token = null;
      this.user = null;
      // Tell the backend to destroy the refresh token cookie
      fetch('/${apiURL}/logout', { method: 'POST', credentials: 'include' });
    },

    async fetchUser() {
      if (!this.token) return;

      const res = await fetch('/${apiURL}/me', {
        headers: { Authorization: `Bearer ${this.token}` },
      });

      if (res.ok) {
        this.user = await res.json();
      } else {
        this.logout(); // Or try token refresh
      }
    },
  },
});
