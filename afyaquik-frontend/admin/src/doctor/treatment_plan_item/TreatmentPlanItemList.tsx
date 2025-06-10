import {List, Datagrid, TextField, EditButton, SelectInput, TextInput, BooleanField} from 'react-admin';



const TreatmentPlanItemList = () => (
    <List>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <BooleanField source="enabled" />
            <EditButton />
        </Datagrid>
    </List>
);

export default TreatmentPlanItemList;
