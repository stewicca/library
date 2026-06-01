import { httpClient } from '@/lib/axios';
import type { WebResponse } from '@/features/auth/types';
import type { CreateBookInput, CreateMagazineInput, LibraryItem } from './types';

/** Transport layer for the catalog endpoints (mirrors `authApi`). */
export const catalogApi = {
  async list(): Promise<LibraryItem[]> {
    const { data } = await httpClient.get<WebResponse<LibraryItem[]>>('/books');
    return data.data ?? [];
  },

  async search(title: string): Promise<LibraryItem[]> {
    const { data } = await httpClient.get<WebResponse<LibraryItem[]>>('/books', {
      params: { title },
    });
    return data.data ?? [];
  },

  async available(): Promise<LibraryItem[]> {
    const { data } = await httpClient.get<WebResponse<LibraryItem[]>>('/books/available');
    return data.data ?? [];
  },

  async titles(): Promise<string[]> {
    const { data } = await httpClient.get<WebResponse<string[]>>('/books/titles');
    return data.data ?? [];
  },

  async addBook(input: CreateBookInput): Promise<LibraryItem> {
    const { data } = await httpClient.post<WebResponse<LibraryItem>>('/books', input);
    return data.data as LibraryItem;
  },

  async addMagazine(input: CreateMagazineInput): Promise<LibraryItem> {
    const { data } = await httpClient.post<WebResponse<LibraryItem>>('/books/magazines', input);
    return data.data as LibraryItem;
  },
};
