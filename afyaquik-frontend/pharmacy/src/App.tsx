import React from 'react';
import './App.css';
import { HashRouter as Router, Route, Routes } from 'react-router-dom';
import PatientDrugList from './patient-drug/PatientDrugList';
import PatientDrugDetailsPage from './patient-drug/PatientDrugDetailsPage';
import PatientDrugEditPage from './patient-drug/PatientDrugEditPage';
import PatientDrugAddPage from './patient-drug/PatientDrugAddPage';

import PatientAssignmentList from './patient/PatientAssignmentList';
import PatientAssignmentDetailsPage from './patient/PatientAssignmentDetailsPage';
import HomePage from './HomePage';
import DrugList from './drug/DrugList';
import DrugDetailsPage from './drug/DrugDetailsPage';
import {AuthGuard, Header, ToastProvider} from "@afyaquik/shared";
import VisitAssign from './visit/VisitAssign';
import VisitDetailsPage from './visit/VisitDetailsPage';

function App() {
  return (
      <ToastProvider>
        <AuthGuard requiredRoles={['PHARMACIST']}>
          <Header homeUrl="/client/pharmacy/index.html" userRole={'PHARMACIST'} />
          <div className="container my-4">
            <Routes>
          <Route path="/patient-drugs/visit/:patientVisitId" element={<PatientDrugList />} />
          <Route path="/patient-drugs/:id/details" element={<PatientDrugDetailsPage />} />
          <Route path="/patient-drugs/:id/edit" element={<PatientDrugEditPage />} />
          <Route path="/patient-drugs/add/:patientVisitId" element={<PatientDrugAddPage />} />

          <Route path="/assignments" element={<PatientAssignmentList />} />
          <Route path="/assignments/:id/details" element={<PatientAssignmentDetailsPage />} />

          <Route path="/visits" element={<PatientAssignmentList />} />
          <Route path="/visits/:id/details" element={<VisitDetailsPage />} />
          <Route path="/visits/:id/assign" element={<VisitAssign />} />
          <Route path="/drugs" element={<DrugList />} />
          <Route path="/drugs/:id/details" element={<DrugDetailsPage />} />
          <Route path="" element={<HomePage />} />
        </Routes>
          </div>
        </AuthGuard>
      </ToastProvider>
  );
}

export default App;
