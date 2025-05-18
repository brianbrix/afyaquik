import {Routes, Route, HashRouter} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import { AuthProvider } from './components/AuthProvider';
import {HomePage, ToastProvider} from "@afyaquik/shared";

export default function App() {
  return (
      <HashRouter>
          <ToastProvider>
          <AuthProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/" element={<HomePage />} />
          </Routes>
      </AuthProvider>
          </ToastProvider>
      </HashRouter>

  );
}
