import {
    Create,
    SimpleForm,
    TextInput,
    BooleanInput,
    ReferenceInput, SelectInput, NumberInput
} from 'react-admin';

const DrugCreate = () => (
   <Create>
       <SimpleForm>
           <TextInput source="name" label="Drug Name" required={true} />
           <TextInput source="brandName" label="Brand Name" />
           <TextInput source="description" label="Description" multiline />

           <ReferenceInput source="categoryId" reference="drugCategories" label="Category">
               <SelectInput optionText="name" optionValue="id" />
           </ReferenceInput>

           <TextInput source="drugForm" label="Drug Form" />
           <TextInput source="strength" label="Strength" />
           <TextInput source="manufacturer" label="Manufacturer" />
           <TextInput source="sampleDosageInstruction" label="Sample Dosage Instruction" multiline />
           <TextInput source="price" label="Price" />
           <NumberInput source="stockQuantity" label="Stock Quantity" />
           <BooleanInput source="isPrescriptionRequired" label="Prescription Required" />
           <TextInput source="atcCode" label="ATC Code" />
           <BooleanInput source="enabled" label="Enabled" defaultValue={true} />
       </SimpleForm>
   </Create>
);

export default DrugCreate;
