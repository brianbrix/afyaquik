import React from 'react';
import './App.css';
import {AuthGuard, Header, ToastProvider} from '@afyaquik/shared';
import {Route, Routes} from "react-router-dom";
import DoctorPatientVisitList from "./patient/DoctorPatientVisitList";
import DoctorPatientVisitDetailsPage from "./patient/DoctorPatientVisitDetailsPage";
import PatientVisitPlanReportsAddPage from "./patient/PatientVisitPlanReportsAddPage";
import DoctorAppointmentDetailsPage from "./patient/DoctorAppointmentDetailsPage";
import DoctorAppointmentList from "./patient/DoctorAppointmentsList";
import TreatmentPlanAddPage from "./patient/TreatmentPlanAddPage";
import DoctorTreatmentPlanAddPage from "./patient/DoctorTreatmentPlanAddPage";

function App() {
  return (
      <ToastProvider>
          <AuthGuard requiredRoles={['DOCTOR']}>
              <Header homeUrl="/client/doctor/index.html" userRole={'DOCTOR'} />
              <div className="container my-4">
                  <Routes>
                      <Route path="" element={<DoctorPatientVisitList />} />
                      <Route path="/visits" element={<DoctorPatientVisitList />} />
                      <Route path="/visits/:id/details" element={<DoctorPatientVisitDetailsPage />} />
                      <Route path="/plans/:id/edit" element={<PatientVisitPlanReportsAddPage />} />
                      <Route path="/appointments/:id/details" element={<DoctorAppointmentDetailsPage />} />
                      <Route path="/appointments" element={<DoctorAppointmentList />} />
                      <Route path="/visits/:id/treatment/plans/create" element={<DoctorTreatmentPlanAddPage />} />

                  </Routes>
              </div>
          </AuthGuard>
      </ToastProvider>
  );
}

export default App;
