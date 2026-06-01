import { describe, it, expect, vi, beforeEach } from 'vitest';
import { makeTestStore } from '@/test/store';
import { selectCatalogItems } from './catalogSlice';
import { fetchCatalogThunk, searchCatalogThunk } from './catalogThunks';
import { catalogApi } from './catalogApi';
import type { LibraryItem } from './types';

vi.mock('./catalogApi', () => ({
  catalogApi: { list: vi.fn(), search: vi.fn(), addBook: vi.fn(), addMagazine: vi.fn() },
}));
vi.mock('react-hot-toast', () => ({ default: { success: vi.fn(), error: vi.fn() } }));

const makeStore = makeTestStore;

const items: LibraryItem[] = [
  { id: 'b1', type: 'BOOK', title: 'Clean Code', author: 'Martin', availableCopies: 2, description: 'x' },
];

describe('catalog thunks', () => {
  beforeEach(() => vi.clearAllMocks());

  it('fetchCatalogThunk loads items into the store and lowers the fetching flag', async () => {
    vi.mocked(catalogApi.list).mockResolvedValue(items);
    const store = makeStore();

    await store.dispatch(fetchCatalogThunk());

    expect(catalogApi.list).toHaveBeenCalledOnce();
    expect(selectCatalogItems(store.getState())).toEqual(items);
    expect(store.getState().ui.isFetching).toBe(false);
  });

  it('searchCatalogThunk with a blank query falls back to listing everything', async () => {
    vi.mocked(catalogApi.list).mockResolvedValue(items);
    const store = makeStore();

    await store.dispatch(searchCatalogThunk('   '));

    expect(catalogApi.list).toHaveBeenCalledOnce();
    expect(catalogApi.search).not.toHaveBeenCalled();
  });

  it('searchCatalogThunk with a query calls the search endpoint', async () => {
    vi.mocked(catalogApi.search).mockResolvedValue(items);
    const store = makeStore();

    await store.dispatch(searchCatalogThunk('clean'));

    expect(catalogApi.search).toHaveBeenCalledWith('clean');
  });
});
