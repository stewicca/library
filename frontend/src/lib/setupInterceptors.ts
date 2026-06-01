import { store } from '@/app/store';
import { selectAccessToken } from '@/features/auth/authSlice';
import { clearCredentials } from '@/features/auth/authSlice';
import { refreshTokenThunk } from '@/features/auth/authThunks';
import { setupAuthInterceptors } from './axios';

/**
 * Wires the axios interceptors to the Redux store. Kept separate from `axios.ts` so that the
 * transport layer never imports the store/thunks (which would create an import cycle).
 * Call once, before rendering.
 */
export function installAuthInterceptors(): void {
  setupAuthInterceptors({
    getAccessToken: () => selectAccessToken(store.getState()),
    refreshAccessToken: async () => {
      const result = await store.dispatch(refreshTokenThunk());
      return refreshTokenThunk.fulfilled.match(result) ? result.payload.accessToken : null;
    },
    onAuthFailure: () => {
      store.dispatch(clearCredentials());
    },
  });
}
