import {
    List,
    Datagrid,
    TextField,
    EditButton,
    BooleanField,
    TextInput,
    ReferenceInput,
    SelectInput, NumberField
} from 'react-admin';
const DrugFilter = [
    <TextInput label="Search by Name" source="name" alwaysOn />,
    <TextInput label="Brand Name" source="brandName" />,
    <TextInput label="Manufacturer" source="manufacturer" />,
    <TextInput label="ATC Code" source="atcCode" />,
    <ReferenceInput label="Category" source="categoryId" reference="drugCategories" allowEmpty>
        <SelectInput optionText="name" />
    </ReferenceInput>
];

const DrugList = () => (
    <List filters={DrugFilter}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <TextField source="brandName" />
            <TextField source="categoryName" />
            <TextField source="manufacturer" />
            <TextField source="drugForm" />
            <TextField source="strength" />
            <TextField source="price" />
            <NumberField source="stockQuantity" />
            <BooleanField source="enabled" />
        </Datagrid>
    </List>
);

export default DrugList;
