import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const DrugCategoryCreate = () => (
   <Create>
      <SimpleForm>
         <TextInput source="name" />
         <TextInput source="description" />
         <BooleanInput source="enabled" />
      </SimpleForm>
   </Create>
);

export default DrugCategoryCreate;
