import {
    Edit,
    SimpleForm,
    TextInput,
    BooleanInput,
    ReferenceInput, SelectInput, NumberInput, required, DateInput, DateTimeInput
} from 'react-admin';
import {validatePositive} from "./DrugInventoryCreate";

const DrugInventoryEdit = () => (
    <Edit resource={"drugs"}>
        <SimpleForm>
            <TextInput source="id" disabled={true} />
            <TextInput name="drugName" source="drugName" label="Drug Name"/>
            <NumberInput source="initialQuantity" label="Initial Quantity" disabled={true} />
            <NumberInput source="currentQuantity" label="Current Quantity" disabled={true} />

            <TextInput source="batchNumber" label="Batch Number" disabled={true} />
            <DateInput source="expiryDate" label="Expiry Date" />
            <DateTimeInput source="receivedDate" label="Received Date" />

            <NumberInput
                source="sellingPrice"
                label="Selling Price"
                validate={[required(), validatePositive]}
            />

            <NumberInput
                source="buyingPrice"
                label="Buying Price"
                validate={[required(), validatePositive]}
            />

            <BooleanInput source="isActive" label="Is Active" defaultValue={true} />

            <TextInput source="supplierName" label="Supplier Name" />
        </SimpleForm>
    </Edit>
);

export default DrugInventoryEdit;
