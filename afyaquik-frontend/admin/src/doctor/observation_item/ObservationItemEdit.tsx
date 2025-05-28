import {BooleanInput, Edit, NumberInput, ReferenceInput, SelectInput, SimpleForm, TextInput} from 'react-admin';

const ObservationItemEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
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
    </Edit>
);

export default ObservationItemEdit;
