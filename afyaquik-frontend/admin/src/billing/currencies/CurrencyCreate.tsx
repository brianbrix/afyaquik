import { Create, SimpleForm, TextInput, BooleanInput } from 'react-admin';

const CurrencyCreate = () => (
    <Create resource="currencies">
        <SimpleForm>
            <TextInput source="name" required={true} />
            <TextInput source="code" required={true} />
            <TextInput source="symbol" required={true} />
            <BooleanInput source="active" defaultValue={false} />
        </SimpleForm>
    </Create>
);

export default CurrencyCreate;
