import { describe, it, expect } from 'vitest';
import uiReducer, { startLoading, stopLoading, setError, clearError } from './uiSlice';

describe('uiSlice', () => {
  it('raises isFetching for reads', () => {
    const state = uiReducer(undefined, startLoading('fetching'));
    expect(state.isFetching).toBe(true);
    expect(state.isSubmitting).toBe(false);
  });

  it('raises isSubmitting for writes', () => {
    const state = uiReducer(undefined, startLoading('submitting'));
    expect(state.isSubmitting).toBe(true);
    expect(state.isFetching).toBe(false);
  });

  it('clears all loading flags on stop', () => {
    const loading = uiReducer(undefined, startLoading('submitting'));
    const state = uiReducer(loading, stopLoading());
    expect(state.isFetching).toBe(false);
    expect(state.isSubmitting).toBe(false);
  });

  it('records and clears errors', () => {
    const errored = uiReducer(undefined, setError('Boom'));
    expect(errored.error).toBe('Boom');
    expect(errored.isSubmitting).toBe(false);
    expect(uiReducer(errored, clearError()).error).toBeNull();
  });
});
