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
import SettingsList from "./settings/SettingsList";
import SettingsEdit from "./settings/SettingsEdit";
import SettingsCreate from "./settings/SettingsCreate";
import StationList from "./stations/StationList";
import StationEdit from "./stations/StationEdit";
import StationCreate from "./stations/StationCreate";
import TriageItemList from "./patient/triage/TriageItemList";
import TriageItemEdit from "./patient/triage/TriageItemEdit";
import TriageItemCreate from "./patient/triage/TriageItemCreate";
import {AuthGuard} from "@afyaquik/shared";
import authProvider from "./authProvider";

const App = () => (
    <AuthGuard requiredRoles={['ADMIN', 'SUPERADMIN']}>
    <Admin dataProvider={dataProvider} authProvider={authProvider}>
      <Resource name="users" list={UserList} edit={UserEdit} create={UserCreate} />
      <Resource name="roles" list={RoleList} edit={RoleEdit} create={RoleCreate} />
      <Resource name="stations" list={StationList} edit={StationEdit} create={StationCreate} />
      <Resource name="generalSettings" list={SettingsList} edit={SettingsEdit} create={SettingsCreate} />
      <Resource name="triageItems" list={TriageItemList} edit={TriageItemEdit} create={TriageItemCreate} />
    </Admin>
    </AuthGuard>
);

export default App;
