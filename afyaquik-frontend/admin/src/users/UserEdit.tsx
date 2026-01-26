import {
    Edit,
    SimpleForm,
    TextInput,
    ReferenceArrayInput,
    CheckboxGroupInput,
    BooleanInput,
    PasswordInput
} from 'react-admin';
import {generatePassword, passwordValidator} from "../utils";
import {useState} from "react";

const UserEdit = () => {
    const [generatedPassword, setGeneratedPassword] = useState("");
    return (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="username" required={true} />
            <TextInput source="firstName" required={true} />
            <TextInput source="secondName" required={true} />
            <TextInput source="lastName" required={true} />
            <TextInput source="email" required={true} />
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <PasswordInput source="password" type="password" validate={passwordValidator} defaultValue={generatedPassword} />
                <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => setGeneratedPassword(generatePassword())}>
                    Generate Password
                </button>
            </div>
            <BooleanInput source="available" />
            <ReferenceArrayInput source="roles" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <ReferenceArrayInput source="stations" reference="stations">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <BooleanInput source="enabled" label="Enabled" />
        </SimpleForm>
    </Edit>
)};

export default UserEdit;
