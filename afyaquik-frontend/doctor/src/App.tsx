import React from 'react';
import logo from './logo.svg';
import './App.css';
import {AuthGuard, Header, ToastProvider} from '@afyaquik/shared';
import {Route, Routes} from "react-router-dom";
import DoctorPatientVisitList from "./patient/DoctorPatientVisitList";
import DoctorPatientVisitDetailsPage from "./patient/DoctorPatientVisitDetailsPage";
import PatientVisitPlanReportsAddPage from "./patient/PatientVisitPlanReportsAddPage";

function App() {
  return (
      <ToastProvider>

          <AuthGuard requiredRoles={['DOCTOR']}>
              <Header homeUrl="/client/doctor/index.html" />
              <div className="container my-4">
                  <Routes>
                      <Route path="" element={<DoctorPatientVisitList />} />
                      <Route path="/visits" element={<DoctorPatientVisitList />} />
                      <Route path="/visits/:id/details" element={<DoctorPatientVisitDetailsPage />} />
                      <Route path="/plans/:id/edit" element={<PatientVisitPlanReportsAddPage />} />

                  </Routes>
              </div>
          </AuthGuard>
      </ToastProvider>
  );
}

export default App;
