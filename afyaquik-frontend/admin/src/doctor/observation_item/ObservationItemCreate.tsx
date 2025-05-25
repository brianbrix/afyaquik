import { Create, SimpleForm, TextInput, SelectInput, NumberInput } from 'react-admin';

const ObservationItemCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" required={true} />
            <SelectInput required={true}
                source="type"
                choices={[
                    { id: 'SYSTEMIC', name: 'Systemic' },
                    { id: 'PHYSICAL', name: 'Physical' },
                    { id: 'OTHER', name: 'Other' }
                ]}
            />
            <NumberInput source="price" />
        </SimpleForm>
    </Create>
);


export default ObservationItemCreate;
