import toast from 'react-hot-toast';
import { createAppAsyncThunk } from '@/app/createAppAsyncThunk';
import { extractMessage } from '@/lib/extractMessage';
import { loansApi } from './loansApi';
import type { CreateLoanInput, Loan } from './types';

/** Load loan history, most recent first. */
export const fetchLoansThunk = createAppAsyncThunk<Loan[], void>(
  'loans/fetch',
  async (_arg, { rejectWithValue }) => {
    try {
      return await loansApi.history();
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to load loans'));
    }
  },
  { uiType: 'fetching' },
);

/** Download the loan history as a CSV report and return its text content. */
export const exportLoansCsvThunk = createAppAsyncThunk<string, void>(
  'loans/exportCsv',
  async (_arg, { rejectWithValue }) => {
    try {
      return await loansApi.exportReportCsv();
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to export report'));
    }
  },
  { uiType: 'fetching' },
);

/** Record a borrowing transaction. */
export const recordLoanThunk = createAppAsyncThunk<Loan, CreateLoanInput>(
  'loans/record',
  async (input, { rejectWithValue }) => {
    try {
      const loan = await loansApi.record(input);
      toast.success(`Loan recorded — due ${loan.dueDate}`);
      return loan;
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to record loan'));
    }
  },
  { uiType: 'submitting', guardFlag: 'isSubmitting' },
);
