import { useAuthStore } from '@/stores/auth.ts';
import { ApiError } from '@/utils/types.ts';

export const apiURL = 'http://localhost:8080';

interface RequestOptions<Req> {
  body?: Req;
  responseType?: 'json' | 'blob';
  headers?: HeadersInit;
  method?: 'GET' | 'POST' | 'PATCH' | 'DELETE';
}

async function request<Res, Req = undefined, TError = any>(
  endpoint: string,
  { method, body, responseType = 'json', headers }: RequestOptions<Req>,
): Promise<Res> {
  const auth = useAuthStore();
  const input = `${apiURL}${endpoint}`;
  const buildHeaders = () => {
    const h = new Headers(headers);
    if (auth.accessToken) h.set('Authorization', `Bearer ${auth.accessToken}`);
    if (body instanceof FormData) {
      // let browser set multipart boundary
    } else if (responseType === 'json') {
      h.set('Content-Type', 'application/json');
    }
    return h;
  };

  const doFetch = () =>
    fetch(input, {
      method,
      headers: buildHeaders(),
      // credentials: 'include',
      body: body instanceof FormData ? body : body != null ? JSON.stringify(body) : undefined,
    });

  let res = await doFetch();

  // 401 = token expired? try refresh once
  if (res.status === 401) {
    await auth.refreshToken();
    if (auth.isAuthenticated) {
      const h = buildHeaders();
      h.set('Authorization', `Bearer ${auth.accessToken}`);
      console.log('[API] Refreshing token...', h);
      res = await fetch(input, {
        method,
        headers: h,
        body: body instanceof FormData ? body : body != null ? JSON.stringify(body) : undefined,
      });
    } else {
      // auth.logout();
      throw new ApiError(res.status, 'Token expired and could not be refreshed.', {});
    }
  }

  // OK? parse, else throw
  if (res.ok) {
    if (responseType === 'blob') {
      return (await res.blob()) as Res;
    } else {
      return (await res.json()) as Res;
    }
  } else {
    // try to parse error body as JSON
    let errData: any;
    try {
      errData = await res.json();
    } catch {
      /* ignore */
    }
    throw new ApiError<TError>(res.status, `HTTP ${res.status} ${res.statusText}`, errData);
  }
}

export const api = {
  get: <T>(endpoint: string) => request<T>(endpoint, { method: 'GET' }),
  post: <In, Out>(endpoint: string, body: In) =>
    request<Out, In>(endpoint, { method: 'POST', body }),
  patch: <In, Out>(endpoint: string, body: In) =>
    request<Out, In>(endpoint, { method: 'PATCH', body }),
  delete: <T = void>(endpoint: string) => request<T>(endpoint, { method: 'DELETE' }),

  // your two “media” endpoints:
  upload: (endpoint: string, form: FormData) =>
    request<Blob, FormData>(endpoint, {
      method: 'POST',
      body: form,
      responseType: 'blob',
    }),
  download: (endpoint: string) => request<Blob>(endpoint, { method: 'GET', responseType: 'blob' }),
};
