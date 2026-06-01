// Canonical role values, mirroring the backend `UserRole` enum names.
export const ROLES = ['ADMIN', 'LIBRARIAN', 'MEMBER'] as const;

export type Role = (typeof ROLES)[number];

export const isRole = (value: unknown): value is Role =>
  typeof value === 'string' && (ROLES as readonly string[]).includes(value);

export const isAdmin = (role: Role | null): boolean => role === 'ADMIN';
export const isLibrarian = (role: Role | null): boolean => role === 'LIBRARIAN';
export const isMember = (role: Role | null): boolean => role === 'MEMBER';

/** Roles allowed into the staff-facing area of the app. Tune as the app grows. */
export const STAFF_ROLES: Role[] = ['ADMIN', 'LIBRARIAN'];

export const hasAnyRole = (role: Role | null, allowed: Role[]): boolean =>
  role !== null && allowed.includes(role);
