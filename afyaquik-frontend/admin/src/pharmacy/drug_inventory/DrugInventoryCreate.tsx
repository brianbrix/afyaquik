import {
    Create,
    SimpleForm,
    TextInput,
    BooleanInput,
    ReferenceInput, SelectInput, NumberInput, required, DateInput, DateTimeInput
} from 'react-admin';

export const validatePositive = (value: any) => {
    if (value <= 0) {
        return 'Must 1 or greater';
    }
};
const DrugInventoryCreate = () => (
   <Create>
       <SimpleForm>
           <ReferenceInput source="drugId" reference="drugs" label="Drug" isRequired={true}>
               <SelectInput optionText="name" optionValue="id" />
           </ReferenceInput>

           <NumberInput source="currentQuantity" label="Current Quantity" step="0.01" />
           <NumberInput step="0.01"
               source="initialQuantity"
               label="Initial Quantity"
               validate={[required(), validatePositive]}
           />

           <TextInput source="batchNumber" label="Batch Number" />

           <DateInput source="expiryDate" label="Expiry Date" />
           <DateTimeInput source="receivedDate" label="Received Date" />

           <NumberInput step="0.01"
               source="sellingPrice"
               label="Selling Price"
               validate={[required(), validatePositive]}
           />

           <NumberInput step="0.01"
               source="buyingPrice"
               label="Buying Price"
               validate={[required(), validatePositive]}
           />

           <BooleanInput source="isActive" label="Is Active" defaultValue={true} />

           <TextInput source="drugName" label="Drug Name (read-only)" disabled />
           <TextInput source="supplierName" label="Supplier Name" />
       </SimpleForm>
   </Create>
);

export default DrugInventoryCreate;
