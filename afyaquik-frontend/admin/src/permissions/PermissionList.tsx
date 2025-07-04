import { List, Datagrid, TextField, EditButton, BooleanField, TextInput } from 'react-admin';

const PermissionFilters = [
    <TextInput label="Search by URL pattern" source="urlPattern" alwaysOn />,
    <TextInput label="Search by description" source="description" alwaysOn />,
];

const PermissionList = () => (
    <List filters={PermissionFilters}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="urlPattern" />
            <TextField source="description" />
            <BooleanField source="enabled" />
            <EditButton />
        </Datagrid>
    </List>
);

export default PermissionList;
