import { Edit, SimpleForm, TextInput, NumberInput, BooleanInput, ReferenceInput, SelectInput } from 'react-admin';

const BillingItemEdit = () => (
    <Edit resource={"billingItems"}>
        <SimpleForm>
            <TextInput source="id" disabled={true} />
            <TextInput source="name" required={true} />
            <TextInput source="description" multiline />
            <NumberInput source="defaultAmount" required={true} />
            <ReferenceInput source="currencyId" reference="currencies">
                <SelectInput optionText={record => record ? `${record.name} (${record.symbol})` : ''} />
            </ReferenceInput>
            <BooleanInput source="active" />
        </SimpleForm>
    </Edit>
);

export default BillingItemEdit;
