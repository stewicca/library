import { describe, it, expect, vi, beforeEach } from 'vitest';
import { makeTestStore } from '@/test/store';
import { loginThunk } from './authThunks';
import { authApi } from './authApi';
import type { AuthPayload } from './types';

// Mock the transport + toast so the thunk logic is tested in isolation.
vi.mock('./authApi', () => ({
  authApi: {
    login: vi.fn(),
    refresh: vi.fn(),
    logout: vi.fn(),
    me: vi.fn(),
  },
}));
vi.mock('react-hot-toast', () => ({
  default: { success: vi.fn(), error: vi.fn() },
}));

const makeStore = makeTestStore;

const payload: AuthPayload = {
  accessToken: 'token-abc',
  tokenType: 'Bearer',
  expiresInSeconds: 900,
  username: 'alice',
  role: 'ADMIN',
};

describe('loginThunk', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('stores credentials and lowers the submitting flag on success', async () => {
    vi.mocked(authApi.login).mockResolvedValue(payload);
    const store = makeStore();

    await store.dispatch(loginThunk({ username: 'alice', password: 'secret' }));

    expect(authApi.login).toHaveBeenCalledWith({ username: 'alice', password: 'secret' });
    expect(store.getState().auth.accessToken).toBe('token-abc');
    expect(store.getState().auth.status).toBe('authenticated');
    expect(store.getState().ui.isSubmitting).toBe(false);
  });

  it('records the error and stays unauthenticated on failure', async () => {
    // Shaped like an AxiosError so `isAxiosError` picks up the server's message.
    vi.mocked(authApi.login).mockRejectedValue({
      isAxiosError: true,
      message: 'Request failed with status code 401',
      response: { data: { message: 'Invalid username or password' } },
    });
    const store = makeStore();

    await store.dispatch(loginThunk({ username: 'alice', password: 'wrong' }));

    expect(store.getState().auth.status).not.toBe('authenticated');
    expect(store.getState().auth.accessToken).toBeNull();
    expect(store.getState().ui.error).toBe('Invalid username or password');
  });
});
