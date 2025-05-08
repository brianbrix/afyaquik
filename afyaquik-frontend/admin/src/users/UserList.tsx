import { List, Datagrid, TextField, EmailField, EditButton } from 'react-admin';

const UserList = () => (
    <List>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="username" />
            <TextField source="firstName" />
            <TextField source="secondName" />
            <TextField source="lastName" />
            <EmailField source="email" />
            <TextField source="roles" />
            <EditButton />
        </Datagrid>
    </List>
);

export default UserList;
