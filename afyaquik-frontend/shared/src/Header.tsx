import React from 'react';
import apiRequest from "./api";

interface HeaderProps {
    homeUrl?:string;
}
const Header: React.FC<HeaderProps>  = ({homeUrl}) => {

    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

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
                <button className="btn btn-light text-primary ms-auto" onClick={handleLogout}>
                    <i className="bi-box-arrow-right me-1"></i> Logout
                </button>
            ) : (
                <button className="btn btn-light text-primary ms-auto" onClick={handleLogin}>
                    <i className="bi-box-arrow-in-right me-1"></i> Login
                </button>
            )}


        </nav>
    );
};

export default Header;
