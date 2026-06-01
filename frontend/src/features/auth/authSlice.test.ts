import { describe, it, expect } from 'vitest';
import authReducer, {
  setCredentials,
  clearCredentials,
  selectIsAuthenticated,
  selectRole,
} from './authSlice';
import type { AuthPayload } from './types';
import type { RootState } from '@/app/store';

const payload: AuthPayload = {
  accessToken: 'token-abc',
  tokenType: 'Bearer',
  expiresInSeconds: 900,
  username: 'alice',
  role: 'LIBRARIAN',
};

describe('authSlice', () => {
  it('starts unauthenticated in the idle state', () => {
    const state = authReducer(undefined, { type: '@@INIT' });
    expect(state.accessToken).toBeNull();
    expect(state.status).toBe('idle');
  });

  it('stores credentials and marks the session authenticated', () => {
    const state = authReducer(undefined, setCredentials(payload));
    expect(state.accessToken).toBe('token-abc');
    expect(state.role).toBe('LIBRARIAN');
    expect(state.username).toBe('alice');
    expect(state.status).toBe('authenticated');
  });

  it('clears credentials on logout', () => {
    const authenticated = authReducer(undefined, setCredentials(payload));
    const state = authReducer(authenticated, clearCredentials());
    expect(state.accessToken).toBeNull();
    expect(state.role).toBeNull();
    expect(state.status).toBe('unauthenticated');
  });

  it('selectors read auth state', () => {
    const authenticated = authReducer(undefined, setCredentials(payload));
    const root = { auth: authenticated } as RootState;
    expect(selectIsAuthenticated(root)).toBe(true);
    expect(selectRole(root)).toBe('LIBRARIAN');
  });
});
