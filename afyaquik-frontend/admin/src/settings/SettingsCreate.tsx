import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const SettingsCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="settingKey" />
            <TextInput source="settingValue" />
        </SimpleForm>
    </Create>
);

export default SettingsCreate;
