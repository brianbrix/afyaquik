import {CheckboxGroupInput, Create, ReferenceArrayInput, SimpleForm, TextInput} from 'react-admin';

const StationCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" />
            <ReferenceArrayInput source="allowedRoles" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
        </SimpleForm>
    </Create>
);

export default StationCreate;
