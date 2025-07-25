import React from 'react';
import './App.css';
import {AuthGuard, Header, PatientAssignForm, ToastProvider} from '@afyaquik/shared';
import {Route, Routes} from "react-router-dom";
import DoctorPatientVisitList from "./patient/DoctorPatientVisitList";
import DoctorPatientVisitDetailsPage from "./patient/DoctorPatientVisitDetailsPage";
import ObservationReportsAddPage from "./patient/ObservationReportsAddPage";
import DoctorAppointmentDetailsPage from "./patient/DoctorAppointmentDetailsPage";
import DoctorAppointmentList from "./patient/DoctorAppointmentsList";
import DoctorTreatmentPlanAddPage from "./patient/DoctorTreatmentPlanAddPage";
import DoctorVisitAssign from "./patient/DoctorVisitAssign";

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
                      <Route path="/visits/:id/assign" element={<DoctorVisitAssign />} />
                      <Route path="/assignments/:id/edit" element={<ObservationReportsAddPage />} />
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
