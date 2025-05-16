import { List, Datagrid, TextField, EditButton } from 'react-admin';

const StationList = () => (
    <List pagination={false}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <EditButton />
        </Datagrid>
    </List>
);

export default StationList;
