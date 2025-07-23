import React from 'react';
import Header from "./Header";

export interface AfyaQuikModule {
    name: string;
    path: string;
    description: string;
    icon: string;
    requiredRoles: string[];
    currentRole?: string;
}


const hasRole = (roles: string[]): boolean => {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    if (!isLoggedIn) {
        return true;
    }
    const currentRole = localStorage.getItem('currentRole');
    console.log("Current Roles", roles, currentRole)
    return roles.includes(currentRole as string)
};

interface BaseHomePageProps {
    modules: AfyaQuikModule[];
}

export const BaseHomePage: React.FC<BaseHomePageProps> = ({modules}) => {
    const handleNavigation = (mod: AfyaQuikModule) => {
        if (hasRole(mod.requiredRoles)) {
            window.location.href = mod.path;
        } else {
            window.location.href = "/client/auth/index.html#/home"
        }
    };

    return (
        <>
            <div className="container py-5">
                <h2 className="text-center mb-4 text-primary">AfyaQuik System Modules</h2>
                <div className="row row-cols-1 row-cols-md-2 g-4">
                    {modules.map((mod) => {
                        return (
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
                                            onClick={() => handleNavigation(mod)}
                                        >
                                            <i className="bi-box-arrow-in-right me-1"></i> Go to {mod.name}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </>
    );
};
