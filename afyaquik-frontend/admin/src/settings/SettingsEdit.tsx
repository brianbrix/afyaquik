import { Edit, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const SettingsEdit = () => (
    <Edit resource={"generalSettings"}>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="settingKey" />
            <TextInput source="settingValue" />
        </SimpleForm>
    </Edit>
);

export default SettingsEdit;
