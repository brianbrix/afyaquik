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
import ObservationItemList from "./doctor/observation_item/ObservationItemList";
import ObservationItemEdit from "./doctor/observation_item/ObservationItemEdit";
import ObservationItemCreate from "./doctor/observation_item/ObservationItemCreate";
import ObservationItemCategoryList from "./doctor/observation_item_category/ObservationItemCategoryList";
import ObservationItemCategoryEdit from "./doctor/observation_item_category/ObservationItemCategoryEdit";
import ObservationItemCategoryCreate from "./doctor/observation_item_category/ObservationItemCategoryCreate";
import TreatmentPlanItemList from "./doctor/treatment_plan_item/TreatmentPlanItemList";
import TreatmentPlanItemCreate from "./doctor/treatment_plan_item/TreatmentPlanItemCreate";
import TreatmentPlanItemEdit from "./doctor/treatment_plan_item/TreatmentPlanItemEdit";
import DrugCategoryList from "./pharmacy/drug_categories/DrugCategoryList";
import DrugCategoryCreate from "./pharmacy/drug_categories/DrugCategoryCreate";
import DrugCategoryEdit from "./pharmacy/drug_categories/DrugCategoryEdit";
import DrugList from "./pharmacy/drugs/DrugList";
import DrugCreate from "./pharmacy/drugs/DrugCreate";
import DrugEdit from "./pharmacy/drugs/DrugEdit";
import DrugInventoryEdit from "./pharmacy/drug_inventory/DrugInventoryEdit";
import DrugInventoryList from "./pharmacy/drug_inventory/DrugInventoryList";
import DrugInventoryCreate from "./pharmacy/drug_inventory/DrugInventoryCreate";

const App = () => (
    <AuthGuard requiredRoles={['ADMIN', 'SUPERADMIN']}>
    <Admin dataProvider={dataProvider} authProvider={authProvider}>
      <Resource name="users" list={UserList} edit={UserEdit} create={UserCreate} />
      <Resource name="roles" list={RoleList} edit={RoleEdit} create={RoleCreate} />
      <Resource name="stations" list={StationList} edit={StationEdit} create={StationCreate} />
      <Resource name="generalSettings" list={SettingsList} edit={SettingsEdit} create={SettingsCreate} />
      <Resource name="triageItems" list={TriageItemList} edit={TriageItemEdit} create={TriageItemCreate} />
        <Resource name="observationItems" list={ObservationItemList} edit={ObservationItemEdit} create={ObservationItemCreate} />
        <Resource name="observationItemCategories" list={ObservationItemCategoryList} edit={ObservationItemCategoryEdit} create={ObservationItemCategoryCreate} />
        <Resource name="treatmentPlanItems" list={TreatmentPlanItemList} create={TreatmentPlanItemCreate} edit={TreatmentPlanItemEdit} />
        <Resource name="drugCategories" list={DrugCategoryList} create={DrugCategoryCreate} edit={DrugCategoryEdit} />
        <Resource name="drugs" list={DrugList} create={DrugCreate} edit={DrugEdit} />
        <Resource name="drugInventory" list={DrugInventoryList} create={DrugInventoryCreate} edit={DrugInventoryEdit} />
    </Admin>
    </AuthGuard>
);

export default App;
