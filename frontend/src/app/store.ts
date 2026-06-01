import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/features/auth/authSlice';
import uiReducer from '@/features/ui/uiSlice';
import catalogReducer from '@/features/catalog/catalogSlice';
import membersReducer from '@/features/members/membersSlice';
import loansReducer from '@/features/loans/loansSlice';
import { asyncStatusMiddleware } from '@/features/ui/asyncStatusMiddleware';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    ui: uiReducer,
    catalog: catalogReducer,
    members: membersReducer,
    loans: loansReducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(asyncStatusMiddleware),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppStore = typeof store;
export type AppDispatch = typeof store.dispatch;
