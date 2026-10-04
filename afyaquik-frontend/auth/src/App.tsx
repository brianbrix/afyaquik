import {Routes, Route, Navigate} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import {AuthGuard, Header, ToastProvider} from "@afyaquik/shared";
import HomePage from "./pages/HomePage";
import ProfilePage from "./pages/ProfilePage";
import React from "react";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";

export default function App() {
  return (
          <ToastProvider>
              <Header />
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/home" element={<AuthGuard><HomePage /></AuthGuard>} />
            <Route path="/" element={<Navigate to="/home" replace />} />
            <Route path="/profile" element={<AuthGuard><ProfilePage /></AuthGuard>} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="*" element={<Navigate to="/home" replace />} />
          </Routes>
          </ToastProvider>

  );
}
