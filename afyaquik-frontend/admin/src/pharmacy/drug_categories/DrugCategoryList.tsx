import { List, Datagrid, TextField, EmailField, EditButton, BooleanField } from 'react-admin';

const DrugCategoryList = () => (
    <List resource={"drugCategories"} pagination={false}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <BooleanField source="enabled" />
        </Datagrid>
    </List>
);

export default DrugCategoryList;
