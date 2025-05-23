import React from 'react';
import {AfyaQuikModule, BaseHomePage} from "@afyaquik/shared";


const modules: AfyaQuikModule[] = [
    { name: 'Patients List', path: '/client/receptionist/index.html#/patients', description: 'Schedule and manage patients', icon: 'bi-calendar-check-fill', requiredRoles: ['RECEPTIONIST'] },
    { name: 'Appointments List', path: '/client/receptionist/index.html#/appointments', description: 'Schedule and manage appointments', icon: 'bi-calendar', requiredRoles: ['RECEPTIONIST'] },
];


const HomePage = () => {


    return (
        <BaseHomePage
            modules = {modules}
        />
    );
};
export default HomePage;
