import { bareClient, httpClient } from '@/lib/axios';
import type { AuthPayload, CurrentUser, LoginCredentials, WebResponse } from './types';

/**
 * Thin transport layer for the auth endpoints. Note `refresh` uses `bareClient` so a failed
 * refresh cannot recurse through the response interceptor.
 */
export const authApi = {
  async login(credentials: LoginCredentials): Promise<AuthPayload> {
    const { data } = await httpClient.post<WebResponse<AuthPayload>>('/auth/login', credentials);
    return data.data as AuthPayload;
  },

  async refresh(): Promise<AuthPayload> {
    const { data } = await bareClient.post<WebResponse<AuthPayload>>('/auth/refresh-token');
    return data.data as AuthPayload;
  },

  async logout(): Promise<void> {
    await httpClient.post('/auth/logout');
  },

  async me(): Promise<CurrentUser> {
    const { data } = await httpClient.get<WebResponse<CurrentUser>>('/auth/me');
    return data.data as CurrentUser;
  },
};
