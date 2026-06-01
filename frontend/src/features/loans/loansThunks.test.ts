import { describe, it, expect, vi, beforeEach } from 'vitest';
import { makeTestStore } from '@/test/store';
import { selectLoans } from './loansSlice';
import { recordLoanThunk } from './loansThunks';
import { loansApi } from './loansApi';
import type { Loan } from './types';

vi.mock('./loansApi', () => ({
  loansApi: { history: vi.fn(), record: vi.fn() },
}));
vi.mock('react-hot-toast', () => ({ default: { success: vi.fn(), error: vi.fn() } }));

const makeStore = makeTestStore;

const loan: Loan = {
  id: 'loan-1',
  memberId: 'm1',
  memberNumber: 'M-001',
  memberName: 'Alice',
  items: [],
  loanDate: '2025-06-01',
  dueDate: '2025-06-08',
};

describe('recordLoanThunk', () => {
  beforeEach(() => vi.clearAllMocks());

  it('stores the recorded loan and clears the submitting flag on success', async () => {
    vi.mocked(loansApi.record).mockResolvedValue(loan);
    const store = makeStore();

    const result = await store.dispatch(recordLoanThunk({ memberId: 'm1', itemIds: ['b1'] }));

    expect(recordLoanThunk.fulfilled.match(result)).toBe(true);
    expect(loansApi.record).toHaveBeenCalledWith({ memberId: 'm1', itemIds: ['b1'] });
    expect(selectLoans(store.getState())).toEqual([loan]);
    expect(store.getState().ui.isSubmitting).toBe(false);
  });

  it('records the server error message on failure', async () => {
    vi.mocked(loansApi.record).mockRejectedValue({
      isAxiosError: true,
      message: 'Request failed with status code 409',
      response: { data: { message: 'No copies available for: Clean Code' } },
    });
    const store = makeStore();

    await store.dispatch(recordLoanThunk({ memberId: 'm1', itemIds: ['b1'] }));

    expect(selectLoans(store.getState())).toEqual([]);
    expect(store.getState().ui.error).toBe('No copies available for: Clean Code');
  });
});
