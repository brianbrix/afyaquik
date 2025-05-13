import {Routes, Route, HashRouter} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import { AuthProvider } from './components/AuthProvider';
import {HomePage} from "@afyaquik/shared";

export default function App() {
  return (
      <AuthProvider>
        <HashRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/" element={<HomePage />} />
          </Routes>
        </HashRouter>
      </AuthProvider>
  );
}
