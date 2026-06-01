import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectRole, selectUsername } from '@/features/auth/authSlice';
import { logoutThunk } from '@/features/auth/authThunks';

/**
 * Placeholder shell that proves the auth + state-management wiring end to end.
 * Replace with the real Library UI later.
 */
export default function DashboardPage() {
  const dispatch = useAppDispatch();
  const username = useAppSelector(selectUsername);
  const role = useAppSelector(selectRole);

  return (
    <div className="min-h-screen bg-base-200 p-8">
      <div className="mx-auto max-w-2xl space-y-4">
        <h1 className="text-3xl font-bold">Dashboard</h1>
        <p className="text-base-content/70">
          Signed in as <span className="font-semibold">{username}</span> ·{' '}
          <span className="badge badge-primary">{role}</span>
        </p>
        <button className="btn btn-outline" onClick={() => dispatch(logoutThunk())}>
          Log out
        </button>
      </div>
    </div>
  );
}
