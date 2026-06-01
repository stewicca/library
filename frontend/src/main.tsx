import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { RouterProvider } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

import './index.css';
import { store } from '@/app/store';
import { router } from '@/routes/router';
import { installAuthInterceptors } from '@/lib/setupInterceptors';
import { bootstrapAuthThunk } from '@/features/auth/authThunks';

// 1) Wire axios <-> store, 2) try to restore the session from the refresh cookie.
installAuthInterceptors();
store.dispatch(bootstrapAuthThunk());

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <Toaster position="top-right" />
      <RouterProvider router={router} />
    </Provider>
  </StrictMode>,
);
