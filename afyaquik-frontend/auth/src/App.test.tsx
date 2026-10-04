import React from 'react';
import { render, screen } from '@testing-library/react';
import LoginPage from './pages/LoginPage';

test('renders the sign-in form with accessible fields', () => {
  render(<LoginPage />);
  expect(screen.getByRole('heading', { name: 'AfyaQuik Login' })).toBeInTheDocument();
  expect(screen.getByLabelText('Username')).toBeInTheDocument();
  expect(screen.getByLabelText('Password')).toHaveAttribute('autocomplete', 'current-password');
  expect(screen.getByRole('link', { name: 'Forgot password?' })).toHaveAttribute('href', '/client/auth/index.html#/forgot-password');
});
