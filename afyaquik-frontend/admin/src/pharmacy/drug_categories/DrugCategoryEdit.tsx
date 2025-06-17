import { Edit, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const DrugCategoryEdit = () => (
    <Edit resource={"drugCategories"}>
        <SimpleForm>
            <TextInput source="id" disabled={true} />
            <TextInput source="name" />
            <TextInput source="description" />
            <BooleanInput source="enabled" />
        </SimpleForm>
    </Edit>
);

export default DrugCategoryEdit;
