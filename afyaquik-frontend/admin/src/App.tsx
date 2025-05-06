import React from 'react';
import './App.css';
import {Admin, Resource} from "react-admin";
import dataProvider from "./dataProvider";
import UserList from "./users/UserList";
import UserEdit from "./users/UserEdit";
import UserCreate from "./users/UserCreate";
import RoleList from './roles/RoleList';
import RoleEdit from "./roles/RoleEdit";
import RoleCreate from "./roles/RoleCreate";
import authProvider from "./authProvider";

const App = () => (
    <Admin dataProvider={dataProvider} authProvider={authProvider}>
      <Resource name="users" list={UserList} edit={UserEdit} create={UserCreate} />
      <Resource name="roles" list={RoleList} edit={RoleEdit} create={RoleCreate} />
    </Admin>
);

export default App;
