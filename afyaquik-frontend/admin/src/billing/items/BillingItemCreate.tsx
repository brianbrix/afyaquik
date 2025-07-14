import { Create, SimpleForm, TextInput, NumberInput, BooleanInput, ReferenceInput, SelectInput } from 'react-admin';

const BillingItemCreate = () => (
    <Create resource={"billingItems"}>
        <SimpleForm>
            <TextInput source="name" required={true} />
            <TextInput source="description" multiline />
            <NumberInput source="defaultAmount" required={true} />
            <ReferenceInput source="currencyId" reference="currencies">
                <SelectInput optionText={record => record ? `${record.name} (${record.symbol})` : ''} />
            </ReferenceInput>
            <BooleanInput source="active" defaultValue={true} />
        </SimpleForm>
    </Create>
);

export default BillingItemCreate;
