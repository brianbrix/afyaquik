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
           <ReferenceInput source="drugFormId" reference="drugForms" label="Drug Form">
               <SelectInput optionText="name" optionValue="id" />
           </ReferenceInput>
           <TextInput source="strength" label="Strength" />
           <TextInput source="manufacturer" label="Manufacturer" />
           <TextInput source="sampleDosageInstruction" label="Sample Dosage Instruction" multiline />
           <NumberInput source="price" label="Price" defaultValue={0.00} step="0.01" />
           <TextInput source="atcCode" label="ATC Code" />
           <BooleanInput source="isPrescriptionRequired" label="Prescription Required" />
           <BooleanInput source="enabled" label="Enabled" defaultValue={true} />
       </SimpleForm>
   </Create>
);

export default DrugCreate;
