import React from 'react';
import './App.css';
import { Navigate, Route, Routes } from 'react-router-dom';
import PatientDrugDetailsPage from './patient-drug/PatientDrugDetailsPage';

import PatientAssignmentList from './patient/PatientAssignmentList';
import PatientAssignmentDetailsPage from './patient/PatientAssignmentDetailsPage';
import HomePage from './HomePage';
import DrugList from './drug/DrugList';
import DrugDetailsPage from './drug/DrugDetailsPage';
import {AlertProvider, AuthGuard, Header, ToastProvider} from "@afyaquik/shared";
import VisitAssign from './visit/VisitAssign';
import VisitDetailsPage from './visit/VisitDetailsPage';
import VisitList from "./visit/VisitList";

function App() {
  return (
      <ToastProvider>
          <AlertProvider>
          <AuthGuard requiredRoles={['PHARMACIST']}>
          <Header homeUrl="/client/pharmacy/index.html" userRole={'PHARMACIST'} />
          <div className="container my-4">
            <Routes>
          <Route path="/patient-drugs/:id/details" element={<PatientDrugDetailsPage />} />

          <Route path="/assignments" element={<PatientAssignmentList />} />
          <Route path="/assignments/:id/details" element={<PatientAssignmentDetailsPage />} />

          <Route path="/visits" element={<VisitList />} />
          <Route path="/visits/:id/details" element={<VisitDetailsPage />} />
          <Route path="/visits/:id/assign" element={<VisitAssign />} />
          <Route path="/drugs" element={<DrugList />} />
          <Route path="/drugs/:id/details" element={<DrugDetailsPage />} />
          <Route path="" element={<HomePage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
          </div>
        </AuthGuard>
          </AlertProvider>
      </ToastProvider>
  );
}

export default App;
