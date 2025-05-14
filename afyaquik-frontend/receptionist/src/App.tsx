import React from 'react';
import './App.css';
import {AuthGuard, Header, HomePage, PatientList, PatientVisit} from "@afyaquik/shared";
import RegisterPatient from "./RegisterPatient";
import {HashRouter, Route, Routes} from "react-router-dom";
import EditPatient from "./EditPatient";
import ReceptionPatientVisit from "./ReceptionPatientVisit";
import ReceptionPatientAssign from "./ReceptionPatientAssign";
import ReceptionPatientList from "./ReceptionPatientList";
import PatientDetailsPage from "./PatientDetailsPage";
import EditVisit from "./EditVisit";
import PatientVisitDetailsPage from "./PatientVisitDetailsPage";

function App() {
  return (
      <HashRouter>
      <AuthGuard requiredRoles={['RECEPTIONIST']}>
        <Header />
        <div className="container my-4">
        <Routes>
          <Route path="" element={<ReceptionPatientList />} />
          <Route path="/patient/add" element={<RegisterPatient />} />
          <Route path="/patient/:id/edit" element={<EditPatient />} />
          <Route path="/patient/:id/details" element={<PatientDetailsPage />} />
          <Route path="/patient/:id/visits/add" element={<ReceptionPatientVisit />} />
          <Route path="/patient/visits/:id/edit" element={<EditVisit />} />
          <Route path="/patient/visits/:id/details" element={<PatientVisitDetailsPage />} />
          <Route path="/patient/visits/:id/assign" element={<ReceptionPatientAssign />} />
        </Routes>
        </div>
      </AuthGuard>
</HashRouter>


);
}

export default App;
