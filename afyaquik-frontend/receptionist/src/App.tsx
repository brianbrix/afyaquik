import React from 'react';
import './App.css';
import {AuthGuard, Header,  } from "@afyaquik/shared";
import PatientRegisterForm from "./PatientRegisterForm";
import {HashRouter, Route, Routes} from "react-router-dom";
import PatientEditForm from "./PatientEditForm";
import ReceptionPatientVisitForm from "./ReceptionPatientVisitForm";
import ReceptionPatientAssignForm from "./ReceptionPatientAssignForm";
import ReceptionPatientList from "./ReceptionPatientList";
import PatientDetailsPage from "./PatientDetailsPage";
import PatientVisitEditForm from "./PatientVisitEditForm";
import PatientVisitDetailsPage from "./PatientVisitDetailsPage";

function App() {
  return (
      <HashRouter>
      <AuthGuard requiredRoles={['RECEPTIONIST']}>
        <Header />
        <div className="container my-4">
        <Routes>
          <Route path="" element={<ReceptionPatientList />} />
          <Route path="/patient/add" element={<PatientRegisterForm />} />
          <Route path="/patient/:id/edit" element={<PatientEditForm />} />
          <Route path="/patient/:id/details" element={<PatientDetailsPage />} />
          <Route path="/patient/:id/visits/add" element={<ReceptionPatientVisitForm />} />
          <Route path="/patient/visits/:id/edit" element={<PatientVisitEditForm />} />
          <Route path="/patient/visits/:id/details" element={<PatientVisitDetailsPage />} />
          <Route path="/patient/visits/:id/assign" element={<ReceptionPatientAssignForm />} />
        </Routes>
        </div>
      </AuthGuard>
</HashRouter>


);
}

export default App;
