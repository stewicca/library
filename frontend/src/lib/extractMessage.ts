import { isAxiosError } from 'axios';
import type { WebResponse } from '@/features/auth/types';

/**
 * Pull a human-readable message out of an error. Prefers the backend's `WebResponse.message`
 * (when the error is an AxiosError), then the axios message, then the supplied fallback.
 */
export function extractMessage(error: unknown, fallback: string): string {
  if (isAxiosError<WebResponse<unknown>>(error)) {
    return error.response?.data?.message ?? error.message ?? fallback;
  }
  return fallback;
}
