import toast from 'react-hot-toast';
import { createAppAsyncThunk } from '@/app/createAppAsyncThunk';
import { extractMessage } from '@/lib/extractMessage';
import { membersApi } from './membersApi';
import type { CreateMemberInput, Member } from './types';

/** Load all registered members. */
export const fetchMembersThunk = createAppAsyncThunk<Member[], void>(
  'members/fetch',
  async (_arg, { rejectWithValue }) => {
    try {
      return await membersApi.list();
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to load members'));
    }
  },
  { uiType: 'fetching' },
);

/** Look up a single member by id. */
export const fetchMemberThunk = createAppAsyncThunk<Member, string>(
  'members/fetchOne',
  async (id, { rejectWithValue }) => {
    try {
      return await membersApi.getById(id);
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to load member'));
    }
  },
  { uiType: 'fetching' },
);

/** Register a new member. */
export const registerMemberThunk = createAppAsyncThunk<Member, CreateMemberInput>(
  'members/register',
  async (input, { rejectWithValue }) => {
    try {
      const member = await membersApi.register(input);
      toast.success(`Registered ${member.name}`);
      return member;
    } catch (error) {
      return rejectWithValue(extractMessage(error, 'Failed to register member'));
    }
  },
  { uiType: 'submitting', guardFlag: 'isSubmitting' },
);
