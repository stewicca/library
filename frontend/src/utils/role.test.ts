import { describe, it, expect } from 'vitest';
import { isRole, isAdmin, hasAnyRole, STAFF_ROLES } from './role';

describe('role utils', () => {
  it('recognises valid role strings', () => {
    expect(isRole('ADMIN')).toBe(true);
    expect(isRole('LIBRARIAN')).toBe(true);
    expect(isRole('GUEST')).toBe(false);
    expect(isRole(123)).toBe(false);
  });

  it('identifies the admin role', () => {
    expect(isAdmin('ADMIN')).toBe(true);
    expect(isAdmin('MEMBER')).toBe(false);
    expect(isAdmin(null)).toBe(false);
  });

  it('checks membership against an allowed list', () => {
    expect(hasAnyRole('LIBRARIAN', STAFF_ROLES)).toBe(true);
    expect(hasAnyRole('MEMBER', STAFF_ROLES)).toBe(false);
    expect(hasAnyRole(null, STAFF_ROLES)).toBe(false);
  });
});
