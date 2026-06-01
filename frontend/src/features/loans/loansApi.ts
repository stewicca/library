import { httpClient } from '@/lib/axios';
import type { WebResponse } from '@/features/auth/types';
import type { CreateLoanInput, Loan } from './types';

/** Transport layer for the loan endpoints (staff only). */
export const loansApi = {
  async history(): Promise<Loan[]> {
    const { data } = await httpClient.get<WebResponse<Loan[]>>('/loans');
    return data.data ?? [];
  },

  async record(input: CreateLoanInput): Promise<Loan> {
    const { data } = await httpClient.post<WebResponse<Loan>>('/loans', input);
    return data.data as Loan;
  },

  async exportReportCsv(): Promise<string> {
    const { data } = await httpClient.get<string>('/loans/report.csv', {
      headers: { Accept: 'text/csv' },
      responseType: 'text',
    });
    return data;
  },
};
