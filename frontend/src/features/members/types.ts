/** Mirrors the backend `MemberResponse`. */
export interface Member {
  id: string;
  memberNumber: string;
  name: string;
  email: string | null;
}

export interface CreateMemberInput {
  memberNumber: string;
  name: string;
  email: string;
}
