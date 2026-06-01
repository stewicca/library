import { isAsyncThunkAction, isPending, isFulfilled, isRejected } from '@reduxjs/toolkit';
import type { Middleware } from '@reduxjs/toolkit';
import toast from 'react-hot-toast';
import type { ThunkMeta } from '@/app/createAppAsyncThunk';
import { setError, startLoading, stopLoading } from './uiSlice';

/**
 * Translates the lifecycle of every async thunk into global UI state, so individual
 * components don't each have to track loading/error. This is the typed successor to the
 * old `asyncActionMiddleware`.
 *
 *   pending   -> startLoading(uiType)
 *   fulfilled -> stopLoading()
 *   rejected  -> setError(message) + toast (unless the thunk opts out)
 */
export const asyncStatusMiddleware: Middleware = (store) => (next) => (action) => {
  if (isAsyncThunkAction(action)) {
    if (isPending(action)) {
      const meta = action.meta as typeof action.meta & ThunkMeta;
      store.dispatch(startLoading(meta.uiType));
    } else if (isFulfilled(action)) {
      store.dispatch(stopLoading());
    } else if (isRejected(action)) {
      // `rejectWithValue(message)` lands in action.payload; otherwise fall back to the error.
      const message =
        (typeof action.payload === 'string' ? action.payload : undefined) ??
        action.error?.message ??
        'Unexpected error occurred';

      // Aborted/condition-skipped thunks aren't real failures — don't toast those.
      if (action.meta.aborted || action.meta.condition) {
        store.dispatch(stopLoading());
      } else {
        store.dispatch(setError(message));
        toast.error(message);
      }
    }
  }

  return next(action);
};
