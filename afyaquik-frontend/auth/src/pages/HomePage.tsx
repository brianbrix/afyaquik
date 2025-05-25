import React from 'react';
import {AfyaQuikModule, BaseHomePage} from "@afyaquik/shared";


const modules: AfyaQuikModule[] = [
    { name: 'Admin Dashboard', path: '/client/admin/index.html', description: 'Manage users, roles and settings', icon: 'bi-gear-fill', requiredRoles: ['ADMIN','SUPERADMIN'] },
    { name: 'Doctor Dashboard', path: '/client/doctor/index.html', description: 'Handle patient doctor and records', icon: 'bi-clipboard2-pulse-fill', requiredRoles: ['DOCTOR'] },
    { name: 'Nurse Dashboard', path: '/client/nurse/index.html', description: 'Handle patient nurse and records', icon: 'bi-clipboard2-pulse-fill', requiredRoles: ['NURSE'] },
    { name: 'Receptionist Dashboard', path: '/client/receptionist/index.html', description: 'Schedule and manage receptionist', icon: 'bi-calendar-check-fill', requiredRoles: ['RECEPTIONIST'] },
    { name: 'Reports Dashboard', path: '/client/reports/index.html', description: 'View and export system reports', icon: 'bi-bar-chart-fill', requiredRoles: ['REPORTS'] },
];


const HomePage = () => {


    return (
        <BaseHomePage
        modules = {modules}
        />
    );
};
export default HomePage;
