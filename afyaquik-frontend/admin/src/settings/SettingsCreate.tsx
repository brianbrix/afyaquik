import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const SettingsCreate = () => (
    <Create resource={"generalSettings"}>
        <SimpleForm>
            <TextInput source="settingKey" />
            <TextInput source="settingValue" />
        </SimpleForm>
    </Create>
);

export default SettingsCreate;
