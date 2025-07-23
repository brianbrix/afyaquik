import {List, Datagrid, TextField, EmailField, EditButton, BooleanField, TextInput, SelectInput} from 'react-admin';

const UserFilters = [
    <TextInput label="Search by username" source="username" alwaysOn />,
    <TextInput label="Search by first name" source="firstName" alwaysOn />,
];
const UserList = () => (
    <List filters={UserFilters}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="username" />
            <TextField source="firstName" />
            <TextField source="secondName" />
            <TextField source="lastName" />
            <EmailField source="email" />
            <BooleanField source="available" />
            <EditButton />
        </Datagrid>
    </List>
);

export default UserList;
