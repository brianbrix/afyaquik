import React from 'react';
import './App.css';
import {AuthGuard, Header, ToastProvider, PORTAL_ROLES} from "@afyaquik/shared";
import ReportsWorkspace from './ReportsWorkspace';

function App() {
  return (
      <ToastProvider>
          <AuthGuard requiredRoles={PORTAL_ROLES.reports}>
              <Header homeUrl="/client/reports/index.html" />
              <ReportsWorkspace />
          </AuthGuard>
      </ToastProvider>
  );
}

export default App;
