import { describe, it, expect, vi, beforeEach } from 'vitest';
import { makeTestStore } from '@/test/store';
import { selectMembers, selectSelectedMember } from './membersSlice';
import { fetchMemberThunk, registerMemberThunk } from './membersThunks';
import { membersApi } from './membersApi';
import type { Member } from './types';

vi.mock('./membersApi', () => ({
  membersApi: { list: vi.fn(), getById: vi.fn(), register: vi.fn() },
}));
vi.mock('react-hot-toast', () => ({ default: { success: vi.fn(), error: vi.fn() } }));

const alice: Member = { id: 'm1', memberNumber: 'M-001', name: 'Alice', email: 'alice@example.com' };

describe('members thunks', () => {
  beforeEach(() => vi.clearAllMocks());

  it('registerMemberThunk stores the new member at the top of the list', async () => {
    vi.mocked(membersApi.register).mockResolvedValue(alice);
    const store = makeTestStore();

    const result = await store.dispatch(
      registerMemberThunk({ memberNumber: 'M-001', name: 'Alice', email: 'alice@example.com' }),
    );

    expect(registerMemberThunk.fulfilled.match(result)).toBe(true);
    expect(selectMembers(store.getState())).toEqual([alice]);
    expect(store.getState().ui.isSubmitting).toBe(false);
  });

  it('fetchMemberThunk stores the selected member', async () => {
    vi.mocked(membersApi.getById).mockResolvedValue(alice);
    const store = makeTestStore();

    await store.dispatch(fetchMemberThunk('m1'));

    expect(membersApi.getById).toHaveBeenCalledWith('m1');
    expect(selectSelectedMember(store.getState())).toEqual(alice);
  });

  it('records the server error on a duplicate member number', async () => {
    vi.mocked(membersApi.register).mockRejectedValue({
      isAxiosError: true,
      response: { data: { message: 'Member number already registered: M-001' } },
    });
    const store = makeTestStore();

    await store.dispatch(
      registerMemberThunk({ memberNumber: 'M-001', name: 'Alice', email: '' }),
    );

    expect(selectMembers(store.getState())).toEqual([]);
    expect(store.getState().ui.error).toBe('Member number already registered: M-001');
  });
});
