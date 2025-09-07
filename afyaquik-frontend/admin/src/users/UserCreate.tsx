import {
    Create,
    SimpleForm,
    TextInput,
    ReferenceArrayInput,
    CheckboxGroupInput,
    BooleanInput,
    PasswordInput
} from 'react-admin';
import {generatePassword, passwordValidator} from "../utils";
import {useState} from "react";

const UserCreate = () => {
    const [generatedPassword, setGeneratedPassword] = useState("");
    return (
        <Create>
            <SimpleForm>
                <TextInput source="username" />
                <TextInput source="firstName" required={true} />
                <TextInput source="secondName" required={true} />
                <TextInput source="lastName" required={true} />
                <ReferenceArrayInput source="roles" reference="roles">
                    <CheckboxGroupInput optionText="name" optionValue="name" />
                </ReferenceArrayInput>
                <TextInput source="email" required={true}/>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <PasswordInput source="password" type="password" validate={passwordValidator} defaultValue={generatedPassword} />
                    <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => setGeneratedPassword(generatePassword())}>
                        Generate Password
                    </button>
                </div>
                <BooleanInput source="enabled" label="Enabled" />
            </SimpleForm>
        </Create>
    );
};
export default UserCreate;
