import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/hooks';
import { loginThunk } from '@/features/auth/authThunks';
import { selectIsAuthenticated } from '@/features/auth/authSlice';
import { selectIsSubmitting } from '@/features/ui/uiSlice';

const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
});

type LoginForm = z.infer<typeof loginSchema>;

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
    if (isAuthenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const onSubmit = (values: LoginForm) => {
    dispatch(loginThunk(values));
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-base-200 p-4">
      <div className="card w-full max-w-sm bg-base-100 shadow-xl">
        <form className="card-body" onSubmit={handleSubmit(onSubmit)} noValidate>
          <h1 className="card-title justify-center text-2xl">Library</h1>

          <label className="form-control w-full">
            <span className="label-text">Username</span>
            <input
              type="text"
              autoComplete="username"
              className="input input-bordered w-full"
              {...register('username')}
            />
            {errors.username && (
              <span className="mt-1 text-sm text-error">{errors.username.message}</span>
            )}
          </label>

          <label className="form-control w-full">
            <span className="label-text">Password</span>
            <input
              type="password"
              autoComplete="current-password"
              className="input input-bordered w-full"
              {...register('password')}
            />
            {errors.password && (
              <span className="mt-1 text-sm text-error">{errors.password.message}</span>
            )}
          </label>

          <button type="submit" className="btn btn-primary mt-2" disabled={isSubmitting}>
            {isSubmitting ? <span className="loading loading-spinner loading-sm" /> : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  );
}
