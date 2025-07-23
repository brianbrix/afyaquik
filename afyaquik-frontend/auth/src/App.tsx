import {Routes, Route, HashRouter} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import { AuthProvider } from './components/AuthProvider';
import {AuthGuard, Header, ToastProvider} from "@afyaquik/shared";
import HomePage from "./pages/HomePage";
import React from "react";

export default function App() {
  return (
          <ToastProvider>
              <Header />
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/" element={<HomePage />} />
          </Routes>
          </ToastProvider>

  );
}
