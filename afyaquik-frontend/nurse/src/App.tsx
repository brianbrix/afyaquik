import React from 'react';
import './App.css';
import {AuthGuard, Header, ToastProvider, PatientAssignForm, PORTAL_ROLES} from "@afyaquik/shared";
import { Navigate, Route, Routes, useParams } from 'react-router-dom';
import { NursingQueue, TriagePage } from './NursingWorkspace';

function Handoff() {
    const { id } = useParams();
    return <PatientAssignForm visitId={Number(id)} />;
}

function App() {
  return (
      <ToastProvider>
          <AuthGuard requiredRoles={PORTAL_ROLES.nurse}>
              <Header homeUrl="/client/nurse/index.html" userRole={'NURSE'} />
              <Routes>
                  <Route path="/visits" element={<NursingQueue />} />
                  <Route path="/visits/:id/triage" element={<TriagePage />} />
                  <Route path="/visits/:id/details" element={<TriagePage />} />
                  <Route path="/visits/:id/assign" element={<Handoff />} />
                  <Route path="*" element={<Navigate to="/visits" replace />} />
              </Routes>
          </AuthGuard>
      </ToastProvider>
  );
}

export default App;
