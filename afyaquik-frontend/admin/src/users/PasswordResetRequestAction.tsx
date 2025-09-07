import {
    Edit,
    SimpleForm,
    TextInput,
    PasswordInput
} from 'react-admin';
import {confirmPasswordValidator, passwordValidator} from "../utils";



const PasswordResetRequestAction = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="username" disabled />
            <PasswordInput source="password" required={true} validate={passwordValidator} />
            <PasswordInput source="confirmPassword" required={true} validate={confirmPasswordValidator} />
        </SimpleForm>
    </Edit>
);
export default PasswordResetRequestAction;
