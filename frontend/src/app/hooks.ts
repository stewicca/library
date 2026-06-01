import { useDispatch, useSelector } from 'react-redux';
import type { AppDispatch, RootState } from './store';

// Use these throughout the app instead of the plain `useDispatch`/`useSelector`
// so dispatch knows about thunks and selectors are fully typed.
export const useAppDispatch = useDispatch.withTypes<AppDispatch>();
export const useAppSelector = useSelector.withTypes<RootState>();
