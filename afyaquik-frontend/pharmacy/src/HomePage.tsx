import React from 'react';
import {AfyaQuikModule, BaseHomePage} from "@afyaquik/shared";


const modules: AfyaQuikModule[] = [
    { name: 'Drug Inventory', path: '/client/pharmacy/index.html#/drugs', description: 'View all available drugs and quantities in inventory', icon: 'bi-capsule', requiredRoles: ['PHARMACIST'] },
    { name: 'My Patient Assignments', path: '/client/pharmacy/index.html#/assignments', description: 'Patient assignments for pharmacy', icon: 'bi-calendar', requiredRoles: ['PHARMACIST'] },
    { name: 'My Patient Visits', path: '/client/pharmacy/index.html#/visits', description: 'Patient visits for pharmacy', icon: 'bi-calendar', requiredRoles: ['PHARMACIST'] },
];


const HomePage = () => {


    return (
        <BaseHomePage
            modules = {modules}
        />
    );
};
export default HomePage;
