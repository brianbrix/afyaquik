import React from 'react';
import apiRequest from "./api";

const Header = () => {

    const handleLogout = () => {
        apiRequest('/auth/logout',{
            method: 'POST',
            body: JSON.stringify({})
        }).then(() => {
            console.log("Logged out");
            localStorage.clear();
            window.location.href = '/client/auth/index.html#/login';
        });
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary px-3">
            <a className="navbar-brand mb-0 h1" href="/">
                AfyaQuik Health
            </a>

            <button className="btn btn-light text-primary ms-auto" onClick={handleLogout}>
                <i className="bi-box-arrow-right me-1"></i> Logout
            </button>
        </nav>
    );
};

export default Header;
