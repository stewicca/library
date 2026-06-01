import type { Role } from '@/utils/role';

/** Mirrors the backend `WebResponse<T>` envelope. */
export interface WebResponse<T> {
  status: number;
  message: string;
  data?: T;
  errors?: string[];
}

/** Mirrors the backend `AuthResponse`. The refresh token is never in the body (cookie only). */
export interface AuthPayload {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  username: string;
  role: Role;
}

export interface CurrentUser {
  id: string;
  username: string;
  role: Role;
}

export interface LoginCredentials {
  username: string;
  password: string;
}
