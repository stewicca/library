import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/features/auth/authSlice';
import uiReducer from '@/features/ui/uiSlice';
import catalogReducer from '@/features/catalog/catalogSlice';
import membersReducer from '@/features/members/membersSlice';
import loansReducer from '@/features/loans/loansSlice';
import { asyncStatusMiddleware } from '@/features/ui/asyncStatusMiddleware';

/**
 * Build a store with the full root-reducer shape (so its state matches `RootState`) and the
 * async-status middleware. Use this in thunk tests instead of a partial store, so dispatching
 * typed app thunks type-checks and selectors can read `store.getState()` directly.
 */
export const makeTestStore = () =>
  configureStore({
    reducer: {
      auth: authReducer,
      ui: uiReducer,
      catalog: catalogReducer,
      members: membersReducer,
      loans: loansReducer,
    },
    middleware: (getDefault) => getDefault().concat(asyncStatusMiddleware),
  });
