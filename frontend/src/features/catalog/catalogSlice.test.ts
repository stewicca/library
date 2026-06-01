import { describe, it, expect } from 'vitest';
import catalogReducer, { selectCatalogItems } from './catalogSlice';
import { addBookThunk, fetchCatalogThunk } from './catalogThunks';
import type { LibraryItem } from './types';
import type { RootState } from '@/app/store';

const book: LibraryItem = {
  id: 'b1',
  type: 'BOOK',
  title: 'Clean Code',
  author: 'Martin',
  availableCopies: 2,
  description: 'Book: Clean Code by Martin (ISBN …)',
};

describe('catalogSlice', () => {
  it('starts with an empty catalog', () => {
    const state = catalogReducer(undefined, { type: '@@INIT' });
    expect(state.items).toEqual([]);
  });

  it('stores items when a fetch fulfils', () => {
    const state = catalogReducer(undefined, {
      type: fetchCatalogThunk.fulfilled.type,
      payload: [book],
    });
    expect(state.items).toHaveLength(1);
    expect(selectCatalogItems({ catalog: state } as RootState)).toEqual([book]);
  });

  it('prepends a newly added book', () => {
    const seeded = catalogReducer(undefined, {
      type: fetchCatalogThunk.fulfilled.type,
      payload: [book],
    });
    const magazine: LibraryItem = { ...book, id: 'b2', title: 'New', type: 'MAGAZINE' };
    const state = catalogReducer(seeded, {
      type: addBookThunk.fulfilled.type,
      payload: magazine,
    });
    expect(state.items[0].id).toBe('b2');
    expect(state.items).toHaveLength(2);
  });
});
