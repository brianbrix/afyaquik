import {List, Datagrid, TextField, EmailField, EditButton, BooleanField, TextInput, SelectInput} from 'react-admin';

const UserFilters = [
    <TextInput label="Search by username" source="username" alwaysOn />,
];
const PasswordResetRequestList = () => (
    <List filters={UserFilters}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="username" />
            <TextField source="status" />
            <EditButton />
        </Datagrid>
    </List>
);

export default PasswordResetRequestList;
