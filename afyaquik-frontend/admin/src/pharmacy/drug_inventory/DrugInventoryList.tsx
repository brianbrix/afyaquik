import {
    List,
    Datagrid,
    TextField,
    BooleanField,
    TextInput,
    ReferenceInput,
    SelectInput, NumberField, DateInput, BooleanInput, DateField
} from 'react-admin';
const InventoryFilters = [
    <TextInput label="Drug Name" source="drugName" alwaysOn />,
    <TextInput label="Supplier Name" source="supplierName" />,
    <BooleanInput label="Is Active" source="isActive" />,
    <DateInput label="Expiry Date" source="expiryDate" />,
    <ReferenceInput label="Drug" source="drugId" reference="drugs" allowEmpty>
        <SelectInput optionText="name" optionValue="id" />
    </ReferenceInput>

];


const DrugInventoryList = () => (
    <List filters={InventoryFilters}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="drugName" />
            <TextField source="supplierName" />
            <NumberField source="currentQuantity" />
            <NumberField source="initialQuantity" />
            <TextField source="batchNumber" />
            <DateField source="expiryDate" />
            <DateField source="receivedDate" showTime />
            <NumberField source="sellingPrice" />
            <NumberField source="buyingPrice" />
            <BooleanField source="isActive" />
        </Datagrid>
    </List>
);

export default DrugInventoryList;
