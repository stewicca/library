import { Link } from 'react-router-dom';
import { ArrowIcon } from '@/components/icons';

export default function NotFoundPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center px-6 text-center">
      <p className="animate-fade font-display text-[8rem] font-semibold leading-none tracking-tight text-primary/20 sm:text-[11rem]">
        404
      </p>
      <h1 className="animate-rise -mt-4 font-display text-3xl font-semibold tracking-tight">
        Halaman tak ditemukan
      </h1>
      <p className="animate-rise mt-2 max-w-sm text-base-content/55" style={{ animationDelay: '90ms' }}>
        Rak yang Anda cari sepertinya kosong. Mari kembali ke ruang utama.
      </p>
      <Link
        to="/"
        className="btn btn-primary group mt-7 animate-rise gap-2 shadow-sm"
        style={{ animationDelay: '160ms' }}
      >
        Kembali
        <ArrowIcon className="transition-transform group-hover:translate-x-0.5" />
      </Link>
    </div>
  );
}
