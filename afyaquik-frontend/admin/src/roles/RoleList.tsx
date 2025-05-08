import { List, Datagrid, TextField, EditButton } from 'react-admin';

const RoleList = () => (
    <List>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <EditButton />
        </Datagrid>
    </List>
);

export default RoleList;
