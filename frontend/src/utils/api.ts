import { useAuthStore } from '@/stores/auth';

export async function authFetch(input: RequestInfo, init: RequestInit = {}) {
  const auth = useAuthStore();

  const makeRequest = async () => {
    const headers = init.headers ? new Headers(init.headers) : new Headers();
    if (auth.token) {
      headers.set('Authorization', `Bearer ${auth.token}`);
    }

    return fetch(input, {
      ...init,
      headers,
      credentials: 'include',
    });
  };

  let response = await makeRequest();

  if (response.status === 401) {
    // Try refreshing the token
    const refreshRes = await fetch('/api/refresh', {
      method: 'POST',
      credentials: 'include',
    });

    if (refreshRes.ok) {
      const data = await refreshRes.json();
      auth.token = data.accessToken;

      // Retry original request
      response = await makeRequest();
    } else {
      // Refresh failed, log out
      auth.logout();
      throw new Error('Session expired');
    }
  }

  return response;
}
