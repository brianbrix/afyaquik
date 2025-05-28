import {
    Create,
    SimpleForm,
    TextInput,
    SelectInput,
    NumberInput,
    ReferenceArrayInput,
    CheckboxGroupInput, ReferenceInput, BooleanInput
} from 'react-admin';

const ObservationItemCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" required={true} />
            <TextInput source="description" />
            <ReferenceInput isRequired={true} source="category" reference="observationItemCategories">
                <SelectInput optionText="name" optionValue="id" />
            </ReferenceInput>
            <ReferenceInput isRequired={true} source="station" reference="stations">
                <SelectInput optionText="name" optionValue="id" />
            </ReferenceInput>
            <NumberInput source="price" />
            <BooleanInput name={"mandatory"} source={"mandatory"} label={"Is Mandatory"}/>
        </SimpleForm>
    </Create>
);


export default ObservationItemCreate;
