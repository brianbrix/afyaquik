import React from 'react';
import './App.css';
import {AppointmentDetailsPage, AppointmentList, AuthGuard, Header, ToastProvider, AlertProvider,} from "@afyaquik/shared";
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
import ReceptionAppointmentDetailsPage from "./appointment/ReceptionAppointmentDetailsPage";

function App() {
  return (
        <ToastProvider>
          <AlertProvider>
            <AuthGuard requiredRoles={['RECEPTIONIST']}>
              <Header homeUrl="/client/receptionist/index.html" userRole={'RECEPTIONIST'} />
              <div className="container my-4">
                <Routes>
                  <Route path="" element={<HomePage />} />
                  <Route path="/patients" element={<ReceptionPatientList />} />
                  <Route path="/appointments" element={<AppointmentList />} />
                  <Route path="/patients/add" element={<PatientRegisterForm />} />
                  <Route path="/patients/:id/edit" element={<PatientEditForm />} />
                  <Route path="/patients/:id/details" element={<PatientDetailsPage />} />
                  <Route path="/patients/:id/visits/add" element={<ReceptionPatientVisitForm />} />
                  <Route path="/visits/:id/edit" element={<PatientVisitEditForm />} />
                  <Route path="/visits/:id/details" element={<PatientVisitDetailsPage />} />
                  <Route path="/visits/:id/assign" element={<ReceptionPatientAssignForm />} />

                  <Route path="/patients/:id/appointments/add" element={<AppointmentCreateForm />} />
                  <Route path="/appointments/:id/edit" element={<AppointmentEditForm />} />
                  <Route path="/appointments/:id/details" element={<ReceptionAppointmentDetailsPage />} />
                </Routes>
              </div>
            </AuthGuard>
          </AlertProvider>
        </ToastProvider>

);
}

export default App;
