import React from 'react';
import apiRequest from "./api";
import {NotificationsBell} from "./index";

interface HeaderProps {
    homeUrl?:string;
    userRole?:string;
}
const Header: React.FC<HeaderProps>  = ({homeUrl, userRole}) => {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    let currentRole = localStorage.getItem('currentRole') || 'USER';

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
    const handleLogin = () => {
        window.location.href = '/client/auth/index.html#/login';
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary px-3">
            {homeUrl ?
                (<a className="navbar-brand mb-0 h1" href={homeUrl}>
                    AfyaQuik Health
                </a>):
                (<a className="navbar-brand mb-0 h1" href="/">
                    AfyaQuik Health
                </a>)
            }
            {isLoggedIn ? (
                <><NotificationsBell userId= {Number(localStorage.getItem('userId'))} userRole={userRole|| currentRole} />
                    <button className="btn btn-light text-primary ms-auto" onClick={handleLogout}>
                        <i className="bi-box-arrow-right me-1"></i> Logout
                    </button>
                </>
            ) : (
                <button className="btn btn-light text-primary ms-auto" onClick={handleLogin}>
                    <i className="bi-box-arrow-in-right me-1"></i> Login
                </button>
            )}


        </nav>
    );
};

export default Header;
