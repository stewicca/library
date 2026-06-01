import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectCatalogItems } from '@/features/catalog/catalogSlice';
import { fetchCatalogThunk } from '@/features/catalog/catalogThunks';
import { selectMembers } from '@/features/members/membersSlice';
import { fetchMembersThunk } from '@/features/members/membersThunks';
import { recordLoanThunk } from '@/features/loans/loansThunks';
import { selectIsSubmitting } from '@/features/ui/uiSlice';
import AppLayout, { PageHeader } from '@/components/AppLayout';

/** Staff-only form to record a borrowing transaction. */
export default function LoanFormPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const members = useAppSelector(selectMembers);
  const items = useAppSelector(selectCatalogItems);
  const isSubmitting = useAppSelector(selectIsSubmitting);

  const [memberId, setMemberId] = useState('');
  const [selectedItems, setSelectedItems] = useState<string[]>([]);

  useEffect(() => {
    dispatch(fetchMembersThunk());
    dispatch(fetchCatalogThunk());
  }, [dispatch]);

  const toggleItem = (id: string) => {
    setSelectedItems((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!memberId || selectedItems.length === 0) return;
    const result = await dispatch(recordLoanThunk({ memberId, itemIds: selectedItems }));
    if (recordLoanThunk.fulfilled.match(result)) {
      navigate('/loans');
    }
  };

  const available = items.filter((item) => item.availableCopies > 0);
  const canSubmit = memberId !== '' && selectedItems.length > 0 && !isSubmitting;

  return (
    <AppLayout>
      <PageHeader eyebrow="Peminjaman · Baru" title="Catat peminjaman" />

      <form
        className="animate-rise max-w-2xl space-y-6 rounded-box border border-base-300/70 bg-base-100 p-7 sm:p-8"
        style={{ animationDelay: '80ms' }}
        onSubmit={onSubmit}
      >
        <label className="block">
          <span className="mb-1.5 block text-xs font-semibold uppercase tracking-[0.16em] text-base-content/60">
            Anggota
          </span>
          <select
            className="select select-bordered w-full bg-base-100 focus:border-primary"
            value={memberId}
            onChange={(e) => setMemberId(e.target.value)}
          >
            <option value="" disabled>
              Pilih anggota…
            </option>
            {members.map((m) => (
              <option key={m.id} value={m.id}>
                {m.memberNumber} — {m.name}
              </option>
            ))}
          </select>
        </label>

        <div>
          <div className="mb-2 flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-[0.16em] text-base-content/60">
              Koleksi dipinjam
            </span>
            <span className="text-xs text-base-content/45">{selectedItems.length} dipilih</span>
          </div>
          <div className="max-h-72 divide-y divide-base-200 overflow-y-auto rounded-field border border-base-300">
            {available.map((item) => {
              const checked = selectedItems.includes(item.id);
              return (
                <label
                  key={item.id}
                  className={`flex cursor-pointer items-center gap-3 px-4 py-3 transition-colors ${
                    checked ? 'bg-primary/5' : 'hover:bg-base-200/60'
                  }`}
                >
                  <input
                    type="checkbox"
                    className="checkbox checkbox-primary checkbox-sm"
                    checked={checked}
                    onChange={() => toggleItem(item.id)}
                  />
                  <span className="flex-1">
                    <span className="font-display font-medium">{item.title}</span>{' '}
                    <span className="text-[0.62rem] font-semibold uppercase tracking-[0.14em] text-secondary">
                      {item.type === 'BOOK' ? 'Buku' : 'Majalah'}
                    </span>
                  </span>
                  <span className="text-sm text-base-content/45">{item.availableCopies} tersisa</span>
                </label>
              );
            })}
            {available.length === 0 && (
              <p className="px-4 py-10 text-center text-sm text-base-content/50">
                Tidak ada koleksi yang tersedia saat ini.
              </p>
            )}
          </div>
          <p className="mt-2 text-xs text-base-content/45">
            Jatuh tempo otomatis 7 hari sejak tanggal peminjaman.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button type="submit" className="btn btn-primary gap-2 shadow-sm" disabled={!canSubmit}>
            {isSubmitting ? <span className="loading loading-spinner loading-sm" /> : 'Catat peminjaman'}
          </button>
          <button type="button" className="btn btn-ghost" onClick={() => navigate('/loans')}>
            Batal
          </button>
        </div>
      </form>
    </AppLayout>
  );
}
