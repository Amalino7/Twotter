import { defineStore } from 'pinia';

export const apiURL = 'http://localhost:8080/';

/**
 * Defines the structure of a user object based on the API response.
 */
export interface User {
  id: string;
  name: string;
  email: string;
  createdAt: object;
  updatedAt: object;
  keycloakId: string;
  bio: string;
  displayName: string;
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: null as string | null,
    user: null as User | null, // Add user to the state
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
     * Clears the access token and user from the state, effectively logging the user out.
     */
    logout() {
      this.accessToken = null;
      this.user = null;
    },
    /**
     * Fetches user data from the API and stores it.
     */
    async fetchUser() {
      if (!this.accessToken) {
        console.error('Cannot fetch user without an access token.');
        return;
      }

      try {
        const response = await fetch(`${apiURL}users/me`, {
          headers: {
            Authorization: `Bearer ${this.accessToken}`,
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error(`Failed to fetch user data with status: ${response.status}`);
        }

        this.user = await response.json();
      } catch (error) {
        console.error('An error occurred while fetching user data:', error);
        // If we can't get user data, the login is incomplete/invalid.
        this.logout();
      }
    },
  },
});
