import {
    Edit,
    SimpleForm,
    TextInput,
    BooleanInput,
    ReferenceInput, SelectInput, NumberInput
} from 'react-admin';

const DrugEdit = () => (
    <Edit resource={"drugs"}>
        <SimpleForm>
            <TextInput name={"id"} source="id" disabled={true}/>
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
            <NumberInput source="currentPrice" label="Current Price" />
            <TextInput source="manufacturer" label="Manufacturer" />
            <TextInput source="sampleDosageInstruction" label="Sample Dosage Instruction" multiline />
            <TextInput source="price" label="Price" />
            <BooleanInput source="isPrescriptionRequired" label="Prescription Required" />
            <TextInput source="atcCode" label="ATC Code" />
            <BooleanInput source="enabled" label="Enabled" defaultValue={true} />
        </SimpleForm>
    </Edit>
);

export default DrugEdit;
