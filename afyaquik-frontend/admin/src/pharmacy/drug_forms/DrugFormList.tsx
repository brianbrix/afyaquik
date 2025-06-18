import { List, Datagrid, TextField, EmailField, EditButton, BooleanField } from 'react-admin';

const DrugFormList = () => (
    <List resource={"drugForms"} pagination={false}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
        </Datagrid>
    </List>
);

export default DrugFormList;
