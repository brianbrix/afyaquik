import {CheckboxGroupInput, Edit, ReferenceArrayInput, SimpleForm, TextInput} from 'react-admin';

const StationEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="name" />
            <ReferenceArrayInput source="allowedRoles" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
        </SimpleForm>
    </Edit>
);

export default StationEdit;
