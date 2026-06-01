import { useEffect, type ReactNode } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { loginThunk } from '@/features/auth/authThunks';
import { selectIsAuthenticated } from '@/features/auth/authSlice';
import { selectIsSubmitting } from '@/features/ui/uiSlice';
import { Brand } from '@/components/AppLayout';
import { ArrowIcon } from '@/components/icons';

const loginSchema = z.object({
  username: z.string().min(1, 'Username wajib diisi'),
  password: z.string().min(1, 'Password wajib diisi'),
});

type LoginForm = z.infer<typeof loginSchema>;

const DEMO_ACCOUNTS = [
  { user: 'admin', pass: 'admin123', role: 'Admin' },
  { user: 'librarian', pass: 'librarian123', role: 'Petugas' },
  { user: 'member', pass: 'member123', role: 'Anggota' },
];

export default function LoginPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const isSubmitting = useAppSelector(selectIsSubmitting);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>({ resolver: zodResolver(loginSchema) });

  useEffect(() => {
    if (isAuthenticated) navigate('/dashboard', { replace: true });
  }, [isAuthenticated, navigate]);

  const onSubmit = (values: LoginForm) => {
    dispatch(loginThunk(values));
  };

  return (
    <div className="grid min-h-screen lg:grid-cols-[1.05fr_1fr]">
      {/* Editorial hero */}
      <aside className="relative hidden flex-col justify-between overflow-hidden bg-neutral p-12 text-neutral-content lg:flex">
        <div
          className="pointer-events-none absolute inset-0 opacity-[0.14]"
          style={{
            backgroundImage:
              'radial-gradient(circle at 1px 1px, currentColor 1px, transparent 0)',
            backgroundSize: '22px 22px',
          }}
        />
        <div className="absolute -right-24 -top-24 size-80 rounded-full bg-secondary/20 blur-3xl" />
        <div className="absolute -bottom-32 -left-16 size-96 rounded-full bg-primary/30 blur-3xl" />

        <div className="relative animate-fade">
          <Brand />
        </div>

        <div className="relative max-w-md animate-rise" style={{ animationDelay: '120ms' }}>
          <p className="mb-5 text-xs font-semibold uppercase tracking-[0.34em] text-secondary">
            Est. MMXXVI
          </p>
          <h1 className="font-display text-5xl font-medium leading-[1.05] tracking-tight">
            Tempat setiap koleksi menemukan pembacanya.
          </h1>
          <p className="mt-6 text-base leading-relaxed text-neutral-content/70">
            Kelola katalog, anggota, dan peminjaman dalam satu ruang yang tenang dan tertata —
            seperti rak buku yang selalu rapi.
          </p>
        </div>

        <div className="relative animate-fade text-sm text-neutral-content/55" style={{ animationDelay: '320ms' }}>
          <span className="font-display text-2xl italic text-secondary/90">“</span> Sebuah ruangan
          tanpa buku bagai tubuh tanpa jiwa.
        </div>
      </aside>

      {/* Form */}
      <main className="flex items-center justify-center px-6 py-12">
        <div className="w-full max-w-sm">
          <div className="mb-10 lg:hidden">
            <Brand />
          </div>

          <div className="animate-rise">
            <p className="mb-1 text-xs font-semibold uppercase tracking-[0.3em] text-secondary">
              Masuk
            </p>
            <h2 className="font-display text-3xl font-semibold tracking-tight">Selamat datang kembali</h2>
            <p className="mt-2 text-sm text-base-content/55">
              Gunakan kredensial Anda untuk membuka sistem.
            </p>
          </div>

          <form
            className="mt-8 animate-rise space-y-5"
            style={{ animationDelay: '90ms' }}
            onSubmit={handleSubmit(onSubmit)}
            noValidate
          >
            <Field label="Username" error={errors.username?.message}>
              <input
                type="text"
                autoComplete="username"
                className="input input-bordered w-full bg-base-100 focus:border-primary"
                {...register('username')}
              />
            </Field>

            <Field label="Password" error={errors.password?.message}>
              <input
                type="password"
                autoComplete="current-password"
                className="input input-bordered w-full bg-base-100 focus:border-primary"
                {...register('password')}
              />
            </Field>

            <button
              type="submit"
              className="btn btn-primary group w-full gap-2 shadow-sm"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <span className="loading loading-spinner loading-sm" />
              ) : (
                <>
                  Masuk
                  <ArrowIcon className="transition-transform group-hover:translate-x-0.5" />
                </>
              )}
            </button>
          </form>

          <div className="mt-10 animate-fade" style={{ animationDelay: '260ms' }}>
            <div className="rule-hairline h-px" />
            <p className="mt-4 text-[0.7rem] font-semibold uppercase tracking-[0.22em] text-base-content/40">
              Akun demo
            </p>
            <div className="mt-2 space-y-1 text-sm">
              {DEMO_ACCOUNTS.map((a) => (
                <div key={a.user} className="flex items-center justify-between">
                  <span className="font-mono text-base-content/70">
                    {a.user} / {a.pass}
                  </span>
                  <span className="text-xs text-base-content/40">{a.role}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

function Field({
  label,
  error,
  children,
}: {
  label: string;
  error?: string;
  children: ReactNode;
}) {
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
