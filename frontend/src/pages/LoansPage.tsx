import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { selectLoans } from '@/features/loans/loansSlice';
import { exportLoansCsvThunk, fetchLoansThunk } from '@/features/loans/loansThunks';
import { selectIsFetching } from '@/features/ui/uiSlice';
import AppLayout, { PageHeader } from '@/components/AppLayout';
import { DownloadIcon, PlusIcon } from '@/components/icons';

/** Staff-only loan history, with a CSV export. */
export default function LoansPage() {
  const dispatch = useAppDispatch();
  const loans = useAppSelector(selectLoans);
  const isFetching = useAppSelector(selectIsFetching);

  useEffect(() => {
    dispatch(fetchLoansThunk());
  }, [dispatch]);

  const onExport = async () => {
    const result = await dispatch(exportLoansCsvThunk());
    if (exportLoansCsvThunk.fulfilled.match(result)) {
      const blob = new Blob([result.payload], { type: 'text/csv' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'loan-report.csv';
      link.click();
      URL.revokeObjectURL(url);
    }
  };

  return (
    <AppLayout>
      <PageHeader
        eyebrow="Transaksi"
        title="Riwayat peminjaman"
        actions={
          <>
            <button
              className="btn btn-ghost btn-sm gap-1.5"
              onClick={onExport}
              disabled={loans.length === 0}
            >
              <DownloadIcon /> Ekspor CSV
            </button>
            <Link to="/loans/new" className="btn btn-primary btn-sm gap-1.5 shadow-sm">
              <PlusIcon /> Catat pinjam
            </Link>
          </>
        }
      />

      <div className="animate-rise overflow-hidden rounded-box border border-base-300/70 bg-base-100" style={{ animationDelay: '80ms' }}>
        <table className="table">
          <thead>
            <tr className="border-base-300/70 text-[0.68rem] uppercase tracking-[0.16em] text-base-content/45">
              <th>Anggota</th>
              <th>Koleksi</th>
              <th>Dipinjam</th>
              <th>Jatuh tempo</th>
            </tr>
          </thead>
          <tbody>
            {loans.map((loan) => (
              <tr key={loan.id} className="border-base-200 transition-colors hover:bg-base-200/50">
                <td>
                  <span className="font-display font-medium">{loan.memberName}</span>
                  <span className="block font-mono text-xs text-base-content/50">{loan.memberNumber}</span>
                </td>
                <td className="text-base-content/70">{loan.items.map((i) => i.title).join(', ')}</td>
                <td className="tabular-nums text-base-content/65">{loan.loanDate}</td>
                <td>
                  <span className="rounded-selector bg-secondary/15 px-2 py-1 text-xs font-medium tabular-nums text-secondary-content">
                    {loan.dueDate}
                  </span>
                </td>
              </tr>
            ))}
            {!isFetching && loans.length === 0 && (
              <tr>
                <td colSpan={4} className="py-12 text-center text-base-content/50">
                  Belum ada peminjaman tercatat.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </AppLayout>
  );
}
