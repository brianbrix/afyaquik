import React from 'react';
import {AfyaQuikModule, BaseHomePage, portalUrl, PORTAL_ROLES} from "@afyaquik/shared";

// In dev each module runs on its own port; in production they share the same origin.
const modules: AfyaQuikModule[] = [
    { name: 'Administration', path: portalUrl('admin'), description: 'Staff, departments and facility settings', icon: 'bi-gear-fill', requiredRoles: PORTAL_ROLES.admin },
    { name: 'Consultations', path: portalUrl('doctor'), description: 'Patient visits, clinical notes and prescriptions', icon: 'bi-clipboard2-pulse-fill', requiredRoles: PORTAL_ROLES.doctor },
    { name: 'Nursing & Triage', path: portalUrl('nurse'), description: 'Patient queue and vital signs', icon: 'bi-heart-pulse-fill', requiredRoles: PORTAL_ROLES.nurse },
    { name: 'Pharmacy', path: portalUrl('pharmacy'), description: 'Prescriptions, dispensing and stock', icon: 'bi-capsule-pill', requiredRoles: PORTAL_ROLES.pharmacy },
    { name: 'Reception', path: portalUrl('receptionist'), description: 'Registration, visits and appointments', icon: 'bi-calendar-check-fill', requiredRoles: PORTAL_ROLES.receptionist },
    { name: 'Reports', path: portalUrl('reports'), description: 'Facility activity and service summaries', icon: 'bi-bar-chart-fill', requiredRoles: PORTAL_ROLES.reports },
];

const HomePage = () => <BaseHomePage modules={modules} />;
export default HomePage;
