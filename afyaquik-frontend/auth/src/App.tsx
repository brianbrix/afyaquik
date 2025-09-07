import {Routes, Route} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import {Header, ToastProvider} from "@afyaquik/shared";
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
            <Route path="/home" element={<HomePage />} />
            <Route path="/" element={<HomePage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          </Routes>
          </ToastProvider>

  );
}
