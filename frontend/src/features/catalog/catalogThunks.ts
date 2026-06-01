import toast from 'react-hot-toast';
import { createAppAsyncThunk } from '@/app/createAppAsyncThunk';
import { extractMessage } from '@/lib/extractMessage';
import { catalogApi } from './catalogApi';
import type { CreateBookInput, CreateMagazineInput, LibraryItem } from './types';

/** Load the full catalog. */
export const fetchCatalogThunk = createAppAsyncThunk<LibraryItem[], void>(
  'catalog/fetch',
  async (_arg, { rejectWithValue }) => {
    try {
      return await catalogApi.list();
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to load catalog'));
    }
  },
  { uiType: 'fetching' },
);

/** Search the catalog by title (empty query falls back to the full list). */
export const searchCatalogThunk = createAppAsyncThunk<LibraryItem[], string>(
  'catalog/search',
  async (title, { rejectWithValue }) => {
    try {
      return title.trim() ? await catalogApi.search(title.trim()) : await catalogApi.list();
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Search failed'));
    }
  },
  { uiType: 'fetching' },
);

/** Load only the items that currently have copies available. */
export const fetchAvailableThunk = createAppAsyncThunk<LibraryItem[], void>(
  'catalog/available',
  async (_arg, { rejectWithValue }) => {
    try {
      return await catalogApi.available();
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to load available items'));
    }
  },
  { uiType: 'fetching' },
);

/** Load all catalog titles, sorted alphabetically. */
export const fetchTitlesThunk = createAppAsyncThunk<string[], void>(
  'catalog/titles',
  async (_arg, { rejectWithValue }) => {
    try {
      return await catalogApi.titles();
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to load titles'));
    }
  },
  { uiType: 'fetching' },
);

/** Add a book to the catalog (staff only). */
export const addBookThunk = createAppAsyncThunk<LibraryItem, CreateBookInput>(
  'catalog/addBook',
  async (input, { rejectWithValue }) => {
    try {
      const item = await catalogApi.addBook(input);
      toast.success(`Added "${item.title}"`);
      return item;
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to add book'));
    }
  },
  { uiType: 'submitting', guardFlag: 'isSubmitting' },
);

/** Add a magazine to the catalog (staff only). */
export const addMagazineThunk = createAppAsyncThunk<LibraryItem, CreateMagazineInput>(
  'catalog/addMagazine',
  async (input, { rejectWithValue }) => {
    try {
      const item = await catalogApi.addMagazine(input);
      toast.success(`Added "${item.title}"`);
      return item;
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to add magazine'));
    }
  },
  { uiType: 'submitting', guardFlag: 'isSubmitting' },
);
