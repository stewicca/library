import { Link } from 'react-router-dom';
import { useAppSelector } from '@/app/hooks';
import { selectRole, selectUsername } from '@/features/auth/authSlice';
import { hasAnyRole, STAFF_ROLES } from '@/utils/role';
import AppLayout from '@/components/AppLayout';
import { ArrowIcon, BookIcon, LoanIcon, UsersIcon } from '@/components/icons';
import type { ReactNode } from 'react';

interface Tile {
  to: string;
  label: string;
  desc: string;
  icon: ReactNode;
  staffOnly?: boolean;
}

const TILES: Tile[] = [
  { to: '/catalog', label: 'Katalog', desc: 'Telusuri dan cari seluruh koleksi perpustakaan.', icon: <BookIcon /> },
  { to: '/members', label: 'Anggota', desc: 'Daftarkan dan kelola anggota perpustakaan.', icon: <UsersIcon />, staffOnly: true },
  { to: '/loans', label: 'Peminjaman', desc: 'Catat peminjaman dan tinjau riwayatnya.', icon: <LoanIcon />, staffOnly: true },
];

export default function DashboardPage() {
  const username = useAppSelector(selectUsername);
  const role = useAppSelector(selectRole);
  const isStaff = hasAnyRole(role, STAFF_ROLES);
  const tiles = TILES.filter((t) => !t.staffOnly || isStaff);

  return (
    <AppLayout>
      <section className="animate-rise">
        <p className="mb-1 text-[0.7rem] font-semibold uppercase tracking-[0.3em] text-secondary">
          Ringkasan
        </p>
        <h1 className="font-display text-4xl font-semibold tracking-tight sm:text-5xl">
          Halo, {username}.
        </h1>
        <p className="mt-3 max-w-xl text-base text-base-content/55">
          Anda masuk sebagai{' '}
          <span className="font-medium text-base-content">{role}</span>. Pilih salah satu modul
          untuk mulai bekerja.
        </p>
      </section>

      <div className="mt-12 grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {tiles.map((tile, i) => (
          <Link
            key={tile.to}
            to={tile.to}
            className="group animate-rise relative flex flex-col justify-between overflow-hidden rounded-box border border-base-300/70 bg-base-100 p-6 transition-all duration-300 hover:-translate-y-1 hover:border-primary/40 hover:shadow-xl hover:shadow-primary/5"
            style={{ animationDelay: `${120 + i * 90}ms` }}
          >
            <span className="pointer-events-none absolute right-5 top-5 font-display text-6xl font-semibold text-base-200 transition-colors group-hover:text-primary/10">
              {String(i + 1).padStart(2, '0')}
            </span>
            <span className="grid size-12 place-items-center rounded-field bg-primary/10 text-2xl text-primary">
              {tile.icon}
            </span>
            <div className="mt-8">
              <h2 className="font-display text-2xl font-semibold tracking-tight">{tile.label}</h2>
              <p className="mt-1.5 text-sm text-base-content/55">{tile.desc}</p>
              <span className="mt-4 inline-flex items-center gap-1.5 text-sm font-medium text-primary">
                Buka
                <ArrowIcon className="transition-transform group-hover:translate-x-1" />
              </span>
            </div>
          </Link>
        ))}
      </div>
    </AppLayout>
  );
}
