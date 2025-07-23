import { List, Datagrid, TextField, EditButton, SelectInput, TextInput } from 'react-admin';


const ItemFilters = [
    <TextInput label="Search by name" source="name" alwaysOn />,
];

const ObservationItemCategoryList = () => (
    <List filters={ItemFilters}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <EditButton />
        </Datagrid>
    </List>
);

export default ObservationItemCategoryList;
