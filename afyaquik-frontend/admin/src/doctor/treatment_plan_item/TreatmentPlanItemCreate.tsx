import {Create, SimpleForm, TextInput, SelectInput, NumberInput, BooleanInput} from 'react-admin';

const TreatmentPlanItemCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" required={true} />
            <TextInput source="description" />
            <BooleanInput name={'enabled'} source="enabled"/>

        </SimpleForm>
    </Create>
);


export default TreatmentPlanItemCreate;
