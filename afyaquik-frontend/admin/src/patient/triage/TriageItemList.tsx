import { List, Datagrid, TextField, EmailField, EditButton, BooleanField } from 'react-admin';

const TriageItemLIst = () => (
    <List resource={"triageItems"} pagination={false}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <TextField source="description" />
        </Datagrid>
    </List>
);

export default TriageItemLIst;
