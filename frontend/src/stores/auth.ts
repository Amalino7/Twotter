import { defineStore } from 'pinia';
import keycloak from '@/utils/keycloak.ts';
import { apiURL } from '@/utils/api.ts';

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
    authenticated: false,
    user: null as User | null,
  }),
  getters: {
    /**
     * Checks if a user is authenticated.
     * @returns True if the access token exists, false otherwise.
     */
    isAuthenticated: (state) => state.authenticated,
  },
  actions: {
    async login() {
      await keycloak.login();
    },
    async init() {
      try {
        await keycloak.init({
          pkceMethod: 'S256',
          silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
          onLoad: 'check-sso',
          flow: 'standard',
        });

        this.authenticated = keycloak.authenticated || false;
        this.accessToken = keycloak.token ?? null;
        if (this.authenticated) {
          await this.fetchUser();
        }

        keycloak.onTokenExpired = this.refreshToken;
      } catch (err) {
        console.error('Keycloak init failed', err);
        this.authenticated = false;
      }
    },

    async refreshToken() {
      try {
        const refreshed = await keycloak.updateToken(5);
        if (refreshed) {
          console.log('[Auth] Token refreshed');
          this.accessToken = keycloak.token ?? null;
          this.authenticated = true;
        } else {
          console.warn('[Auth] Token not refreshed — still valid');
        }
      } catch (err: any) {
        console.error('[Auth] Token refresh failed, logging out', err);
        await this.logout();
      }
    },

    async logout() {
      // await keycloak.logout();
      if (keycloak.authenticated) {
        await keycloak.logout();
      }
      this.authenticated = false;
      this.accessToken = null;
      this.user = null;
    },

    async fetchUser() {
      if (!this.accessToken) {
        console.error('Cannot fetch user without an access token.');
        return;
      }

      try {
        const response = await fetch(`${apiURL}/users/me`, {
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
        await this.logout();
      }
    },
  },
});
