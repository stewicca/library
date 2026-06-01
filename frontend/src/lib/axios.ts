import axios, {
  AxiosError,
  type AxiosInstance,
  type InternalAxiosRequestConfig,
} from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api/v1';

/**
 * Shared client. `withCredentials` lets the browser carry the HttpOnly refresh-token cookie.
 * Use this for every authenticated request — the interceptors below attach the access token
 * and transparently refresh it on a 401.
 */
export const httpClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

/**
 * Bare client with NO interceptors, used only for the refresh call so we never recurse
 * (a 401 from /refresh-token must not trigger another refresh).
 */
export const bareClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

interface RetriableConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

export interface AuthInterceptorHandlers {
  getAccessToken: () => string | null;
  /** Returns the new access token, or null if refresh failed. */
  refreshAccessToken: () => Promise<string | null>;
  onAuthFailure: () => void;
}

// Ensures concurrent 401s share a single in-flight refresh instead of stampeding the endpoint.
let refreshPromise: Promise<string | null> | null = null;

export function setupAuthInterceptors(handlers: AuthInterceptorHandlers): void {
  httpClient.interceptors.request.use((config) => {
    const token = handlers.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  httpClient.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const original = error.config as RetriableConfig | undefined;

      if (error.response?.status !== 401 || !original || original._retry) {
        return Promise.reject(error);
      }

      original._retry = true;

      refreshPromise ??= handlers.refreshAccessToken().finally(() => {
        refreshPromise = null;
      });
      const newToken = await refreshPromise;

      if (!newToken) {
        handlers.onAuthFailure();
        return Promise.reject(error);
      }

      original.headers.Authorization = `Bearer ${newToken}`;
      return httpClient(original);
    },
  );
}
