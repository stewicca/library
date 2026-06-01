import { createSlice } from '@reduxjs/toolkit';
import type { RootState } from '@/app/store';
import { fetchLoansThunk, recordLoanThunk } from './loansThunks';
import type { Loan } from './types';

interface LoansState {
  loans: Loan[];
}

const initialState: LoansState = {
  loans: [],
};

const loansSlice = createSlice({
  name: 'loans',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchLoansThunk.fulfilled, (state, action) => {
        state.loans = action.payload;
      })
      .addCase(recordLoanThunk.fulfilled, (state, action) => {
        state.loans.unshift(action.payload);
      });
  },
});

export const selectLoans = (state: RootState) => state.loans.loans;

export default loansSlice.reducer;
