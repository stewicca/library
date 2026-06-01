import { createBrowserRouter } from 'react-router-dom';
import AuthRedirect from './AuthRedirect';
import ProtectedRoute from './ProtectedRoute';
import LoginPage from '@/pages/LoginPage';
import DashboardPage from '@/pages/DashboardPage';
import CatalogPage from '@/pages/CatalogPage';
import AddItemPage from '@/pages/AddItemPage';
import MembersPage from '@/pages/MembersPage';
import LoanFormPage from '@/pages/LoanFormPage';
import LoansPage from '@/pages/LoansPage';
import NotFoundPage from '@/pages/NotFoundPage';
import { STAFF_ROLES } from '@/utils/role';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AuthRedirect />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/dashboard',
    element: (
      <ProtectedRoute>
        <DashboardPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/catalog',
    element: (
      <ProtectedRoute>
        <CatalogPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/catalog/new',
    element: (
      <ProtectedRoute allowedRoles={STAFF_ROLES}>
        <AddItemPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/members',
    element: (
      <ProtectedRoute allowedRoles={STAFF_ROLES}>
        <MembersPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/loans',
    element: (
      <ProtectedRoute allowedRoles={STAFF_ROLES}>
        <LoansPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/loans/new',
    element: (
      <ProtectedRoute allowedRoles={STAFF_ROLES}>
        <LoanFormPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '*',
    element: <NotFoundPage />,
  },
]);
