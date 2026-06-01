import type { LibraryItem } from '@/features/catalog/types';

/** Mirrors the backend `LoanResponse`. */
export interface Loan {
  id: string;
  memberId: string;
  memberNumber: string;
  memberName: string;
  items: LibraryItem[];
  loanDate: string;
  dueDate: string;
}

export interface CreateLoanInput {
  memberId: string;
  itemIds: string[];
}
