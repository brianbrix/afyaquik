import {List, Datagrid, TextField, EditButton, SelectInput, TextInput, BooleanField} from 'react-admin';

const itemTypeChoices = [
    { id: 'SYSTEMIC', name: 'Systemic' },
    { id: 'PHYSICAL', name: 'Physical' },
    { id: 'OTHER', name: 'Other' }
];

const ItemFilters = [
    <TextInput label="Search by name" source="name" alwaysOn />,
    <SelectInput label="Type" source="type" choices={itemTypeChoices} />
];

const ObservationItemList = () => (
    <List filters={ItemFilters}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <TextField source="categoryName" />
            <TextField source="stationName" />
            <BooleanField source="mandatory" />
            <EditButton />
        </Datagrid>
    </List>
);

export default ObservationItemList;
