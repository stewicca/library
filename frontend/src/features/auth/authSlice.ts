import { createSlice, type PayloadAction } from '@reduxjs/toolkit';
import type { Role } from '@/utils/role';
import type { RootState } from '@/app/store';
import type { AuthPayload } from './types';

export type AuthStatus = 'idle' | 'bootstrapping' | 'authenticated' | 'unauthenticated';

interface AuthState {
  // Access token is held in memory only (never localStorage) to limit XSS exposure.
  // The refresh token lives in an HttpOnly cookie the JS never sees.
  accessToken: string | null;
  username: string | null;
  role: Role | null;
  status: AuthStatus;
}

const initialState: AuthState = {
  accessToken: null,
  username: null,
  role: null,
  status: 'idle',
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (state, action: PayloadAction<AuthPayload>) => {
      state.accessToken = action.payload.accessToken;
      state.username = action.payload.username;
      state.role = action.payload.role;
      state.status = 'authenticated';
    },
    clearCredentials: (state) => {
      state.accessToken = null;
      state.username = null;
      state.role = null;
      state.status = 'unauthenticated';
    },
    setStatus: (state, action: PayloadAction<AuthStatus>) => {
      state.status = action.payload;
    },
  },
});

export const { setCredentials, clearCredentials, setStatus } = authSlice.actions;

// Selectors
export const selectAccessToken = (state: RootState) => state.auth.accessToken;
export const selectRole = (state: RootState) => state.auth.role;
export const selectUsername = (state: RootState) => state.auth.username;
export const selectAuthStatus = (state: RootState) => state.auth.status;
export const selectIsAuthenticated = (state: RootState) =>
  state.auth.status === 'authenticated' && state.auth.accessToken !== null;

export default authSlice.reducer;
