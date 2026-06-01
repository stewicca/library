import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectCatalogItems, selectCatalogTitles } from '@/features/catalog/catalogSlice';
import {
  fetchAvailableThunk,
  fetchCatalogThunk,
  fetchTitlesThunk,
  searchCatalogThunk,
} from '@/features/catalog/catalogThunks';
import { selectIsFetching } from '@/features/ui/uiSlice';
import { selectRole } from '@/features/auth/authSlice';
import { hasAnyRole, STAFF_ROLES } from '@/utils/role';
import AppLayout, { PageHeader } from '@/components/AppLayout';
import { LoanIcon, PlusIcon, SearchIcon } from '@/components/icons';

export default function CatalogPage() {
  const dispatch = useAppDispatch();
  const items = useAppSelector(selectCatalogItems);
  const titles = useAppSelector(selectCatalogTitles);
  const isFetching = useAppSelector(selectIsFetching);
  const role = useAppSelector(selectRole);
  const isStaff = hasAnyRole(role, STAFF_ROLES);
  const [query, setQuery] = useState('');
  const [availableOnly, setAvailableOnly] = useState(false);

  useEffect(() => {
    dispatch(fetchCatalogThunk());
    dispatch(fetchTitlesThunk());
  }, [dispatch]);

  const onSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setAvailableOnly(false);
    dispatch(searchCatalogThunk(query));
  };

  const onToggleAvailable = () => {
    const next = !availableOnly;
    setAvailableOnly(next);
    dispatch(next ? fetchAvailableThunk() : fetchCatalogThunk());
  };

  return (
    <AppLayout>
      <PageHeader
        eyebrow="Koleksi"
        title="Katalog"
        actions={
          isStaff && (
            <>
              <Link to="/loans/new" className="btn btn-ghost btn-sm gap-1.5">
                <LoanIcon /> Catat pinjam
              </Link>
              <Link to="/catalog/new" className="btn btn-primary btn-sm gap-1.5 shadow-sm">
                <PlusIcon /> Tambah koleksi
              </Link>
            </>
          )
        }
      />

      <div
        className="animate-rise flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"
        style={{ animationDelay: '80ms' }}
      >
        <form className="relative w-full sm:max-w-md" onSubmit={onSearch}>
          <SearchIcon className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-base text-base-content/40" />
          <input
            type="text"
            className="input input-bordered w-full bg-base-100 pl-10 focus:border-primary"
            placeholder="Cari berdasarkan judul…"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </form>

        <div className="flex items-center gap-4">
          <label className="flex cursor-pointer items-center gap-2 text-sm">
            <input
              type="checkbox"
              className="toggle toggle-primary toggle-sm"
              checked={availableOnly}
              onChange={onToggleAvailable}
            />
            Tersedia saja
          </label>

          <details className="dropdown dropdown-end">
            <summary className="btn btn-ghost btn-sm">Judul A–Z</summary>
            <ul className="menu dropdown-content z-20 mt-2 max-h-72 w-64 flex-nowrap overflow-y-auto rounded-box border border-base-300 bg-base-100 p-2 shadow-xl">
              {titles.map((title) => (
                <li key={title}>
                  <span className="text-sm">{title}</span>
                </li>
              ))}
            </ul>
          </details>
        </div>
      </div>

      <div
        className="animate-rise mt-6 overflow-hidden rounded-box border border-base-300/70 bg-base-100"
        style={{ animationDelay: '160ms' }}
      >
        <table className="table">
          <thead>
            <tr className="border-base-300/70 text-[0.68rem] uppercase tracking-[0.16em] text-base-content/45">
              <th>Judul</th>
              <th>Jenis</th>
              <th>Pengarang</th>
              <th className="text-right">Tersedia</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id} className="border-base-200 transition-colors hover:bg-base-200/50">
                <td className="font-display text-base font-medium">{item.title}</td>
                <td>
                  <span className="text-[0.66rem] font-semibold uppercase tracking-[0.14em] text-secondary">
                    {item.type === 'BOOK' ? 'Buku' : 'Majalah'}
                  </span>
                </td>
                <td className="text-base-content/65">{item.author}</td>
                <td className="text-right">
                  <span className="inline-flex items-center gap-2">
                    <span
                      className={`size-1.5 rounded-full ${
                        item.availableCopies > 0 ? 'bg-success' : 'bg-error'
                      }`}
                    />
                    <span className="tabular-nums font-medium">{item.availableCopies}</span>
                  </span>
                </td>
              </tr>
            ))}
            {!isFetching && items.length === 0 && (
              <tr>
                <td colSpan={4} className="py-12 text-center text-base-content/50">
                  Tidak ada koleksi yang cocok.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </AppLayout>
  );
}
