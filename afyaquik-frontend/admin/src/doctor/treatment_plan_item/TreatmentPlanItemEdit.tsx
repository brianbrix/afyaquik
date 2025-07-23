import {BooleanInput, Edit, NumberInput, SelectInput, SimpleForm, TextInput} from 'react-admin';

const TreatmentPlanItemEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="name" required={true} />
            <TextInput source="description" />
            <BooleanInput name={'enabled'} source="enabled"/>

        </SimpleForm>
    </Edit>
);

export default TreatmentPlanItemEdit;
