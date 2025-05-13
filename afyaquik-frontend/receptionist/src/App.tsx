import React from 'react';
import './App.css';
import {AuthGuard, Header, HomePage} from "@afyaquik/shared";
import RegisterPatient from "./RegisterPatient";
import PatientList from "./PatientList";
import {HashRouter, Route, Routes} from "react-router-dom";
import EditPatient from "./EditPatient";
import PatientVisit from "./PatientVisit";

function App() {
  return (
      <HashRouter>
      <AuthGuard requiredRoles={['ROLE_RECEPTIONIST']}>
        <Header />
        <Routes>
          <Route path="" element={<PatientList />} />
          <Route path="/add" element={<RegisterPatient />} />
          <Route path="/edit/:id" element={<EditPatient />} />
          <Route path="/edit/:id" element={<EditPatient />} />
          <Route path="/patient/:id/visits/create" element={<PatientVisit />} />
        </Routes>
      </AuthGuard>
</HashRouter>


);
}

export default App;
