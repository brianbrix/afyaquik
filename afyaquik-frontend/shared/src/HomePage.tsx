import React from 'react';
interface Module {
    name: string;
    path: string;
    description: string;
    icon: string;
}

const modules: Module[] = [
    { name: 'Admin Dashboard', path: '/client/admin/index.html', description: 'Manage users, roles and settings', icon: 'bi-gear-fill' },
    { name: 'Doctor Dashboard', path: '/client/doctor/index.html', description: 'Handle patient doctor and records', icon: 'bi-clipboard2-pulse-fill' },
    { name: 'Receptionist Dashboard', path: '/client/receptionist/index.html', description: 'Schedule and manage receptionist', icon: 'bi-calendar-check-fill' },
    { name: 'Reports Dashboard', path: '/client/reports/index.html', description: 'View and export system reports', icon: 'bi-bar-chart-fill' },
];

const HomePage = () => {
    return (
        <div className="container py-5">
            <h2 className="text-center mb-4 text-primary">AfyaQuik System Modules</h2>
            <div className="row row-cols-1 row-cols-md-2 g-4">
                {modules.map((mod) => (
                    <div className="col" key={mod.name}>
                        <div className="card h-100 shadow-sm">
                            <div className="card-body d-flex flex-column">
                                <h5 className="card-title d-flex align-items-center">
                                    <i className={`${mod.icon} me-2 text-primary`} style={{ fontSize: '1.5rem' }}></i>
                                    {mod.name}
                                </h5>
                                <p className="card-text flex-grow-1">{mod.description}</p>
                                <a href={mod.path} className="btn btn-primary mt-3 w-100">
                                    <i className="bi-box-arrow-in-right me-1"></i> Go to {mod.name}
                                </a>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default HomePage;
