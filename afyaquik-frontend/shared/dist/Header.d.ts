import React from 'react';
interface HeaderProps {
    /** Link for the brand logo — use the module's own home page.
     *  Omit (or leave undefined) when rendering inside the auth/hub module itself. */
    homeUrl?: string;
    userRole?: string;
}
declare const Header: React.FC<HeaderProps>;
export default Header;
