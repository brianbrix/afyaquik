import { Edit, SimpleForm, TextInput, BooleanInput } from 'react-admin';

const CurrencyEdit = () => (
    <Edit resource="currencies">
        <SimpleForm>
            <TextInput source="id" disabled={true} />
            <TextInput source="name" required={true} />
            <TextInput source="code" required={true} />
            <TextInput source="symbol" required={true} />
            <BooleanInput source="active" />
        </SimpleForm>
    </Edit>
);

export default CurrencyEdit;
