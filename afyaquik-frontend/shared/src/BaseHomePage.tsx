import React, { useState } from 'react';
import { portalUrl, selectRole, sessionRoles } from './session';

export interface AfyaQuikModule {
    name: string;
    path: string;
    description: string;
    icon: string;
    requiredRoles: string[];
    currentRole?: string;
}

const LOGIN_URL = portalUrl('auth', '/login');

const hasRole = (roles: string[]): boolean => {
    if (localStorage.getItem('isLoggedIn') !== 'true') return false;
    return sessionRoles().some(role => roles.includes(role));
};

interface BaseHomePageProps {
    modules: AfyaQuikModule[];
}

export const BaseHomePage: React.FC<BaseHomePageProps> = ({modules}) => {
    const [error, setError] = useState('');
    const [opening, setOpening] = useState('');
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

    if (!isLoggedIn) {
        window.location.replace(LOGIN_URL);
        return null;
    }

    const handleNavigation = async (mod: AfyaQuikModule) => {
        if (hasRole(mod.requiredRoles)) {
            setOpening(mod.name);
            setError('');
            try {
                const current = localStorage.getItem('currentRole') || '';
                const role = mod.requiredRoles.includes(current) ? current : sessionRoles().find(value => mod.requiredRoles.includes(value));
                if (role) await selectRole(role);
                window.location.href = mod.path;
            } catch {
                setError('Unable to open this module. Please retry.');
                setOpening('');
            }
        } else {
            window.location.href = LOGIN_URL;
        }
    };

    const visibleModules = modules.filter(mod => hasRole(mod.requiredRoles));

    return (
        <>
            <div className="container py-5">
                <h2 className="text-center mb-4 text-primary">AfyaQuik System Modules</h2>
                {error && <div className="alert alert-warning" role="alert">{error}</div>}
                {visibleModules.length === 0 ? (
                    <p className="text-center text-muted">
                        No modules are assigned to your current role. Contact an administrator.
                    </p>
                ) : (
                    <div className="row row-cols-1 row-cols-md-2 g-4">
                        {visibleModules.map((mod) => (
                            <div className="col" key={mod.name}>
                                <div className="card h-100 shadow-sm">
                                    <div className="card-body d-flex flex-column">
                                        <h5 className="card-title d-flex align-items-center">
                                            <i className={`${mod.icon} me-2 text-primary`}
                                               style={{fontSize: '1.5rem'}}></i>
                                            {mod.name}
                                        </h5>
                                        <p className="card-text flex-grow-1">{mod.description}</p>
                                        <button
                                            className="btn btn-primary mt-3 w-100"
                                            disabled={!!opening}
                                            onClick={() => handleNavigation(mod)}
                                        >
                                            <i className="bi-box-arrow-in-right me-1"></i> Go to {mod.name}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
};
