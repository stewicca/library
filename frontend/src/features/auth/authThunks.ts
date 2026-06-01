import { isAxiosError } from 'axios';
import toast from 'react-hot-toast';
import { createAppAsyncThunk } from '@/app/createAppAsyncThunk';
import { authApi } from './authApi';
import { clearCredentials, setCredentials, setStatus } from './authSlice';
import type { AuthPayload, LoginCredentials, WebResponse } from './types';

function extractMessage(error: unknown, fallback: string): string {
  if (isAxiosError<WebResponse<unknown>>(error)) {
    return error.response?.data?.message ?? error.message ?? fallback;
  }
  return fallback;
}

/** Log in, store credentials in the Redux store, and surface a success toast. */
export const loginThunk = createAppAsyncThunk<AuthPayload, LoginCredentials>(
  'auth/login',
  async (credentials, { dispatch, rejectWithValue }) => {
    try {
      const payload = await authApi.login(credentials);
      dispatch(setCredentials(payload));
      toast.success(`Welcome, ${payload.username}`);
      return payload;
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Login failed'));
    }
  },
  { uiType: 'submitting', guardFlag: 'isSubmitting' },
);

/**
 * Silent token refresh. Used by the axios interceptor and on app bootstrap.
 * Does not toast — failure here is expected when there is simply no active session.
 */
export const refreshTokenThunk = createAppAsyncThunk<AuthPayload>(
  'auth/refresh',
  async (_arg, { dispatch, rejectWithValue }) => {
    try {
      const payload = await authApi.refresh();
      dispatch(setCredentials(payload));
      return payload;
    } catch (error) {
      dispatch(clearCredentials());
      return rejectWithValue(extractMessage(error, 'Session expired'));
    }
  },
);

/** Restore a session on first load by attempting a refresh against the cookie. */
export const bootstrapAuthThunk = createAppAsyncThunk<void>(
  'auth/bootstrap',
  async (_arg, { dispatch }) => {
    dispatch(setStatus('bootstrapping'));
    try {
      const payload = await authApi.refresh();
      dispatch(setCredentials(payload));
    } catch {
      dispatch(clearCredentials());
    }
  },
);

/** Log out server-side (best effort) then clear local credentials. */
export const logoutThunk = createAppAsyncThunk<void>(
  'auth/logout',
  async (_arg, { dispatch }) => {
    try {
      await authApi.logout();
    } catch {
      // Even if the network call fails, clear local state below.
    } finally {
      dispatch(clearCredentials());
    }
  },
  { uiType: 'submitting' },
);
