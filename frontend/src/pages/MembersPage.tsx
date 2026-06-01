import { useEffect, type ReactNode } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import {
  clearSelectedMember,
  selectMembers,
  selectSelectedMember,
} from '@/features/members/membersSlice';
import {
  fetchMemberThunk,
  fetchMembersThunk,
  registerMemberThunk,
} from '@/features/members/membersThunks';
import { selectIsSubmitting } from '@/features/ui/uiSlice';
import AppLayout, { PageHeader } from '@/components/AppLayout';

const schema = z.object({
  memberNumber: z.string().min(1, 'Nomor anggota wajib diisi'),
  name: z.string().min(1, 'Nama wajib diisi'),
  email: z.string().email('Email tidak valid').or(z.literal('')),
});

type MemberForm = z.infer<typeof schema>;

export default function MembersPage() {
  const dispatch = useAppDispatch();
  const members = useAppSelector(selectMembers);
  const selected = useAppSelector(selectSelectedMember);
  const isSubmitting = useAppSelector(selectIsSubmitting);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<MemberForm>({ resolver: zodResolver(schema) });

  useEffect(() => {
    dispatch(fetchMembersThunk());
    return () => {
      dispatch(clearSelectedMember());
    };
  }, [dispatch]);

  const onSubmit = async (values: MemberForm) => {
    const result = await dispatch(registerMemberThunk(values));
    if (registerMemberThunk.fulfilled.match(result)) {
      reset({ memberNumber: '', name: '', email: '' });
    }
  };

  return (
    <AppLayout>
      <PageHeader eyebrow="Keanggotaan" title="Anggota" />

      <div className="grid gap-8 lg:grid-cols-[22rem_1fr]">
        {/* Register form */}
        <form
          className="animate-rise h-fit space-y-5 rounded-box border border-base-300/70 bg-base-100 p-6"
          style={{ animationDelay: '70ms' }}
          onSubmit={handleSubmit(onSubmit)}
          noValidate
        >
          <h2 className="font-display text-2xl font-semibold tracking-tight">Daftarkan anggota</h2>

          <TextField label="Nomor anggota" error={errors.memberNumber?.message}>
            <input className="input input-bordered w-full bg-base-100 focus:border-primary" {...register('memberNumber')} />
          </TextField>
          <TextField label="Nama" error={errors.name?.message}>
            <input className="input input-bordered w-full bg-base-100 focus:border-primary" {...register('name')} />
          </TextField>
          <TextField label="Email" error={errors.email?.message}>
            <input className="input input-bordered w-full bg-base-100 focus:border-primary" {...register('email')} />
          </TextField>

          <button type="submit" className="btn btn-primary w-full shadow-sm" disabled={isSubmitting}>
            {isSubmitting ? <span className="loading loading-spinner loading-sm" /> : 'Daftarkan'}
          </button>
        </form>

        {/* List + detail */}
        <div className="animate-rise space-y-5" style={{ animationDelay: '150ms' }}>
          <div className="overflow-hidden rounded-box border border-base-300/70 bg-base-100">
            <table className="table">
              <thead>
                <tr className="border-base-300/70 text-[0.68rem] uppercase tracking-[0.16em] text-base-content/45">
                  <th>No. Anggota</th>
                  <th>Nama</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {members.map((m) => (
                  <tr key={m.id} className="border-base-200 transition-colors hover:bg-base-200/50">
                    <td className="font-mono text-sm text-base-content/70">{m.memberNumber}</td>
                    <td className="font-display text-base font-medium">{m.name}</td>
                    <td className="text-right">
                      <button
                        className="btn btn-ghost btn-xs text-primary"
                        onClick={() => dispatch(fetchMemberThunk(m.id))}
                      >
                        Detail
                      </button>
                    </td>
                  </tr>
                ))}
                {members.length === 0 && (
                  <tr>
                    <td colSpan={3} className="py-12 text-center text-base-content/50">
                      Belum ada anggota.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {selected && (
            <div className="animate-rise rounded-box border border-primary/30 bg-primary/5 p-6">
              <p className="text-[0.68rem] font-semibold uppercase tracking-[0.18em] text-secondary">
                Detail anggota
              </p>
              <h3 className="mt-1 font-display text-2xl font-semibold tracking-tight">{selected.name}</h3>
              <div className="mt-3 flex flex-wrap gap-x-8 gap-y-1 text-sm text-base-content/70">
                <span>
                  No. anggota: <span className="font-mono">{selected.memberNumber}</span>
                </span>
                <span>{selected.email || 'Tanpa email'}</span>
              </div>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
}

function TextField({ label, error, children }: { label: string; error?: string; children: ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-xs font-semibold uppercase tracking-[0.16em] text-base-content/60">
        {label}
      </span>
      {children}
      {error && <span className="mt-1.5 block text-sm text-error">{error}</span>}
    </label>
  );
}
