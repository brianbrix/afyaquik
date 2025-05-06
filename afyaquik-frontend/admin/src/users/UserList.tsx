import { List, Datagrid, TextField, EmailField, EditButton } from 'react-admin';

const UserList = () => (
    <List>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="username" />
            <TextField source="firstname" />
            <TextField source="secondname" />
            <TextField source="lastname" />
            <EmailField source="email" />
            <TextField source="roles" />
            <EditButton />
        </Datagrid>
    </List>
);

export default UserList;
