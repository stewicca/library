import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectRole, selectUsername } from '@/features/auth/authSlice';
import { logoutThunk } from '@/features/auth/authThunks';
import { hasAnyRole, STAFF_ROLES } from '@/utils/role';
import { BookIcon, GridIcon, LoanIcon, LogoutIcon, UsersIcon } from './icons';

interface NavItem {
  to: string;
  label: string;
  icon: ReactNode;
  staffOnly?: boolean;
}

const NAV: NavItem[] = [
  { to: '/dashboard', label: 'Ringkasan', icon: <GridIcon /> },
  { to: '/catalog', label: 'Katalog', icon: <BookIcon /> },
  { to: '/members', label: 'Anggota', icon: <UsersIcon />, staffOnly: true },
  { to: '/loans', label: 'Peminjaman', icon: <LoanIcon />, staffOnly: true },
];

/** The wordmark + monogram shown in the header and on the auth screen. */
export function Brand({ compact = false }: { compact?: boolean }) {
  return (
    <div className="flex items-center gap-3">
      <span className="grid size-9 place-items-center rounded-[0.6rem] bg-primary font-display text-lg font-semibold text-primary-content shadow-sm ring-1 ring-secondary/40">
        P
      </span>
      {!compact && (
        <span className="leading-none">
          <span className="block font-display text-lg font-semibold tracking-tight">Perpustakaan</span>
          <span className="block text-[0.62rem] font-medium uppercase tracking-[0.28em] text-base-content/50">
            Library System
          </span>
        </span>
      )}
    </div>
  );
}

/** Eyebrow + serif title block used at the top of each page. */
export function PageHeader({
  eyebrow,
  title,
  actions,
}: {
  eyebrow: string;
  title: string;
  actions?: ReactNode;
}) {
  return (
    <header className="animate-rise flex flex-wrap items-end justify-between gap-4 pb-6">
      <div>
        <p className="mb-1 text-[0.7rem] font-semibold uppercase tracking-[0.3em] text-secondary">
          {eyebrow}
        </p>
        <h1 className="font-display text-4xl font-semibold tracking-tight text-base-content sm:text-5xl">
          {title}
        </h1>
      </div>
      {actions && <div className="flex flex-wrap items-center gap-2">{actions}</div>}
    </header>
  );
}

/** Authenticated application shell: brand bar, primary nav, user chip, footer. */
export default function AppLayout({ children }: { children: ReactNode }) {
  const dispatch = useAppDispatch();
  const username = useAppSelector(selectUsername);
  const role = useAppSelector(selectRole);
  const isStaff = hasAnyRole(role, STAFF_ROLES);
  const items = NAV.filter((item) => !item.staffOnly || isStaff);

  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-30 border-b border-base-300/70 bg-base-100/80 backdrop-blur-md">
        <div className="mx-auto flex h-16 max-w-6xl items-center gap-4 px-4 sm:px-6">
          <NavLink to="/dashboard" className="shrink-0">
            <Brand />
          </NavLink>

          <nav className="ml-2 hidden items-center gap-1 md:flex">
            {items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `group flex items-center gap-2 rounded-field px-3 py-2 text-sm font-medium transition-colors ${
                    isActive
                      ? 'bg-primary/10 text-primary'
                      : 'text-base-content/60 hover:bg-base-200 hover:text-base-content'
                  }`
                }
              >
                <span className="text-base">{item.icon}</span>
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="ml-auto flex items-center gap-3">
            <div className="hidden text-right sm:block">
              <p className="text-sm font-semibold leading-tight">{username}</p>
              <p className="text-[0.62rem] font-medium uppercase tracking-[0.18em] text-secondary">
                {role}
              </p>
            </div>
            <span className="grid size-9 place-items-center rounded-full bg-neutral font-display text-sm font-semibold text-neutral-content">
              {(username ?? '?').charAt(0).toUpperCase()}
            </span>
            <button
              onClick={() => dispatch(logoutThunk())}
              className="btn btn-ghost btn-sm gap-2 text-base-content/60 hover:text-error"
              aria-label="Keluar"
            >
              <LogoutIcon />
              <span className="hidden lg:inline">Keluar</span>
            </button>
          </div>
        </div>

        {/* Mobile nav */}
        <nav className="flex items-center gap-1 overflow-x-auto border-t border-base-300/60 px-3 py-2 md:hidden">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `flex shrink-0 items-center gap-1.5 rounded-field px-3 py-1.5 text-sm font-medium ${
                  isActive ? 'bg-primary/10 text-primary' : 'text-base-content/60'
                }`
              }
            >
              {item.icon}
              {item.label}
            </NavLink>
          ))}
        </nav>
      </header>

      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-10 sm:px-6 sm:py-14">{children}</main>

      <footer className="border-t border-base-300/70">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-2 px-4 py-5 text-xs text-base-content/45 sm:px-6">
          <span className="font-display italic">Perpustakaan — sistem katalog &amp; peminjaman</span>
          <span className="uppercase tracking-[0.2em]">Skema Pemrogram · FR.IA.02</span>
        </div>
      </footer>
    </div>
  );
}
