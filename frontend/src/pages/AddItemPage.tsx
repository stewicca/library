import type { ReactNode } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { addBookThunk, addMagazineThunk } from '@/features/catalog/catalogThunks';
import { selectIsSubmitting } from '@/features/ui/uiSlice';
import AppLayout, { PageHeader } from '@/components/AppLayout';

const schema = z
  .object({
    type: z.enum(['BOOK', 'MAGAZINE']),
    title: z.string().min(1, 'Judul wajib diisi'),
    author: z.string().min(1, 'Pengarang wajib diisi'),
    availableCopies: z.coerce.number().int().min(0, 'Tidak boleh negatif'),
    isbn: z.string().optional(),
    edition: z.coerce.number().int().optional(),
  })
  .superRefine((value, ctx) => {
    if (value.type === 'BOOK' && !value.isbn?.trim()) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ['isbn'], message: 'ISBN wajib diisi' });
    }
    if (value.type === 'MAGAZINE' && (!value.edition || value.edition < 1)) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ['edition'], message: 'Edisi minimal 1' });
    }
  });

type AddItemForm = z.infer<typeof schema>;

export default function AddItemPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const isSubmitting = useAppSelector(selectIsSubmitting);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<AddItemForm>({
    resolver: zodResolver(schema),
    defaultValues: { type: 'BOOK', availableCopies: 1 },
  });

  const type = watch('type');

  const onSubmit = async (values: AddItemForm) => {
    const result =
      values.type === 'BOOK'
        ? await dispatch(
            addBookThunk({
              title: values.title,
              author: values.author,
              availableCopies: values.availableCopies,
              isbn: values.isbn ?? '',
            }),
          )
        : await dispatch(
            addMagazineThunk({
              title: values.title,
              author: values.author,
              availableCopies: values.availableCopies,
              edition: values.edition ?? 1,
            }),
          );
    if (addBookThunk.fulfilled.match(result) || addMagazineThunk.fulfilled.match(result)) {
      navigate('/catalog');
    }
  };

  return (
    <AppLayout>
      <PageHeader eyebrow="Katalog · Baru" title="Tambah koleksi" />

      <form
        className="animate-rise max-w-xl space-y-6 rounded-box border border-base-300/70 bg-base-100 p-7 sm:p-8"
        style={{ animationDelay: '80ms' }}
        onSubmit={handleSubmit(onSubmit)}
        noValidate
      >
        <div>
          <FieldLabel>Jenis koleksi</FieldLabel>
          <div className="join w-full">
            {(['BOOK', 'MAGAZINE'] as const).map((t) => (
              <label key={t} className="join-item flex-1 cursor-pointer">
                <input type="radio" value={t} className="peer sr-only" {...register('type')} />
                <span className="flex items-center justify-center rounded-field border border-base-300 px-4 py-2.5 text-sm font-medium transition-colors peer-checked:border-primary peer-checked:bg-primary peer-checked:text-primary-content">
                  {t === 'BOOK' ? 'Buku' : 'Majalah'}
                </span>
              </label>
            ))}
          </div>
        </div>

        <TextField label="Judul" error={errors.title?.message}>
          <input className="input input-bordered w-full bg-base-100 focus:border-primary" {...register('title')} />
        </TextField>

        <TextField label="Pengarang" error={errors.author?.message}>
          <input className="input input-bordered w-full bg-base-100 focus:border-primary" {...register('author')} />
        </TextField>

        <TextField label="Jumlah salinan" error={errors.availableCopies?.message}>
          <input
            type="number"
            min={0}
            className="input input-bordered w-full bg-base-100 focus:border-primary"
            {...register('availableCopies')}
          />
        </TextField>

        {type === 'BOOK' ? (
          <TextField label="ISBN" error={errors.isbn?.message}>
            <input className="input input-bordered w-full bg-base-100 focus:border-primary" {...register('isbn')} />
          </TextField>
        ) : (
          <TextField label="Edisi" error={errors.edition?.message}>
            <input
              type="number"
              min={1}
              className="input input-bordered w-full bg-base-100 focus:border-primary"
              {...register('edition')}
            />
          </TextField>
        )}

        <div className="flex items-center gap-3 pt-1">
          <button type="submit" className="btn btn-primary gap-2 shadow-sm" disabled={isSubmitting}>
            {isSubmitting ? <span className="loading loading-spinner loading-sm" /> : 'Simpan koleksi'}
          </button>
          <button type="button" className="btn btn-ghost" onClick={() => navigate('/catalog')}>
            Batal
          </button>
        </div>
      </form>
    </AppLayout>
  );
}

function FieldLabel({ children }: { children: ReactNode }) {
  return (
    <span className="mb-1.5 block text-xs font-semibold uppercase tracking-[0.16em] text-base-content/60">
      {children}
    </span>
  );
}

function TextField({ label, error, children }: { label: string; error?: string; children: ReactNode }) {
  return (
    <label className="block">
      <FieldLabel>{label}</FieldLabel>
      {children}
      {error && <span className="mt-1.5 block text-sm text-error">{error}</span>}
    </label>
  );
}
