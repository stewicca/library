/** Mirrors the backend `LibraryItemResponse`. */
export interface LibraryItem {
  id: string;
  type: 'BOOK' | 'MAGAZINE';
  title: string;
  author: string;
  availableCopies: number;
  description: string;
}

export interface CreateBookInput {
  title: string;
  author: string;
  availableCopies: number;
  isbn: string;
}

export interface CreateMagazineInput {
  title: string;
  author: string;
  availableCopies: number;
  edition: number;
}
