import React from 'react';

const Header = () => {

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRoles');
        window.location.href = '/client/auth/index.html#/login';
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary px-3">
            <h1 className="navbar-brand mb-0 h1">AfyaQuik Health</h1>

            <button className="btn btn-light text-primary ms-auto" onClick={handleLogout}>
                <i className="bi-box-arrow-right me-1"></i> Logout
            </button>
        </nav>
    );
};

export default Header;
