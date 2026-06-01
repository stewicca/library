import { createSlice } from '@reduxjs/toolkit';
import type { RootState } from '@/app/store';
import {
  addBookThunk,
  addMagazineThunk,
  fetchAvailableThunk,
  fetchCatalogThunk,
  fetchTitlesThunk,
  searchCatalogThunk,
} from './catalogThunks';
import type { LibraryItem } from './types';

interface CatalogState {
  items: LibraryItem[];
  titles: string[];
}

const initialState: CatalogState = {
  items: [],
  titles: [],
};

const catalogSlice = createSlice({
  name: 'catalog',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchCatalogThunk.fulfilled, (state, action) => {
        state.items = action.payload;
      })
      .addCase(searchCatalogThunk.fulfilled, (state, action) => {
        state.items = action.payload;
      })
      .addCase(fetchAvailableThunk.fulfilled, (state, action) => {
        state.items = action.payload;
      })
      .addCase(fetchTitlesThunk.fulfilled, (state, action) => {
        state.titles = action.payload;
      })
      .addCase(addBookThunk.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
      })
      .addCase(addMagazineThunk.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
      });
  },
});

export const selectCatalogItems = (state: RootState) => state.catalog.items;
export const selectCatalogTitles = (state: RootState) => state.catalog.titles;

export default catalogSlice.reducer;
