import { defineStore } from 'pinia';

export const apiURL = 'http://localhost:8080/';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: null as string | null,
  }),
  getters: {
    /**
     * Checks if a user is authenticated.
     * @returns True if the access token exists, false otherwise.
     */
    isAuthenticated: (state) => !!state.accessToken,
  },
  actions: {
    /**
     * Sets the access token in the state.
     * @param token The access token to store.
     */
    setAccessToken(token: string) {
      this.accessToken = token;
    },
    /**
     * Clears the access token from the state, effectively logging the user out.
     */
    logout() {
      this.accessToken = null;
    },
  },
});
