/** Branded full-screen loading state shown while the session bootstraps. */
export default function LoadingScreen() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-5">
      <span className="grid size-12 animate-pulse place-items-center rounded-[0.7rem] bg-primary font-display text-2xl font-semibold text-primary-content shadow-sm ring-1 ring-secondary/40">
        P
      </span>
      <span className="text-xs font-medium uppercase tracking-[0.3em] text-base-content/40">
        Memuat…
      </span>
    </div>
  );
}
