import { httpClient } from '@/lib/axios';
import type { WebResponse } from '@/features/auth/types';
import type { CreateMemberInput, Member } from './types';

/** Transport layer for the member endpoints (staff only). */
export const membersApi = {
  async list(): Promise<Member[]> {
    const { data } = await httpClient.get<WebResponse<Member[]>>('/members');
    return data.data ?? [];
  },

  async getById(id: string): Promise<Member> {
    const { data } = await httpClient.get<WebResponse<Member>>(`/members/${id}`);
    return data.data as Member;
  },

  async register(input: CreateMemberInput): Promise<Member> {
    const { data } = await httpClient.post<WebResponse<Member>>('/members', input);
    return data.data as Member;
  },
};
