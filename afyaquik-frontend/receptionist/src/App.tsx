import React from 'react';
import './App.css';
import {AppointmentList, AuthGuard, Header, ToastProvider,} from "@afyaquik/shared";
import {HashRouter, Route, Routes} from "react-router-dom";

import HomePage from "./HomePage";
import ReceptionPatientList from "./patient/ReceptionPatientList";
import PatientRegisterForm from "./patient/PatientRegisterForm";
import PatientEditForm from "./patient/PatientEditForm";
import PatientDetailsPage from "./patient/PatientDetailsPage";
import ReceptionPatientVisitForm from "./patient/ReceptionPatientVisitForm";
import PatientVisitEditForm from "./patient/PatientVisitEditForm";
import PatientVisitDetailsPage from "./patient/PatientVisitDetailsPage";
import ReceptionPatientAssignForm from "./patient/ReceptionPatientAssignForm";
import AppointmentCreateForm from "./appointment/AppointmentCreateForm";
import AppointmentEditForm from "./appointment/AppointmentEditForm";
import AppointmentDetailsPage from "./appointment/AppointmentDetailsPage";

function App() {
  return (
        <ToastProvider>

        <AuthGuard requiredRoles={['RECEPTIONIST']}>
        <Header homeUrl="/client/receptionist/index.html" />
        <div className="container my-4">
        <Routes>
          <Route path="" element={<HomePage />} />
          <Route path="/patients" element={<ReceptionPatientList />} />
          <Route path="/appointments" element={<AppointmentList />} />
          <Route path="/patients/add" element={<PatientRegisterForm />} />
          <Route path="/patients/:id/edit" element={<PatientEditForm />} />
          <Route path="/patients/:id/details" element={<PatientDetailsPage />} />
          <Route path="/patients/:id/visits/add" element={<ReceptionPatientVisitForm />} />
          <Route path="/patients/visits/:id/edit" element={<PatientVisitEditForm />} />
          <Route path="/patients/visits/:id/details" element={<PatientVisitDetailsPage />} />
          <Route path="/patients/visits/:id/assign" element={<ReceptionPatientAssignForm />} />

          <Route path="/patients/:id/appointments/add" element={<AppointmentCreateForm />} />
          <Route path="/appointments/:id/edit" element={<AppointmentEditForm />} />
          <Route path="/appointments/:id/details" element={<AppointmentDetailsPage />} />
        </Routes>
        </div>
      </AuthGuard>
        </ToastProvider>

);
}

export default App;
