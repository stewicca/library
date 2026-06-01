import { createSlice } from '@reduxjs/toolkit';
import type { RootState } from '@/app/store';
import { fetchMemberThunk, fetchMembersThunk, registerMemberThunk } from './membersThunks';
import type { Member } from './types';

interface MembersState {
  members: Member[];
  selected: Member | null;
}

const initialState: MembersState = {
  members: [],
  selected: null,
};

const membersSlice = createSlice({
  name: 'members',
  initialState,
  reducers: {
    clearSelectedMember: (state) => {
      state.selected = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMembersThunk.fulfilled, (state, action) => {
        state.members = action.payload;
      })
      .addCase(fetchMemberThunk.fulfilled, (state, action) => {
        state.selected = action.payload;
      })
      .addCase(registerMemberThunk.fulfilled, (state, action) => {
        state.members.unshift(action.payload);
      });
  },
});

export const { clearSelectedMember } = membersSlice.actions;

export const selectMembers = (state: RootState) => state.members.members;
export const selectSelectedMember = (state: RootState) => state.members.selected;

export default membersSlice.reducer;
