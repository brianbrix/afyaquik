import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const DrugFormCreate = () => (
   <Create>
      <SimpleForm>
         <TextInput source="name" />
         <TextInput source="description" />
      </SimpleForm>
   </Create>
);

export default DrugFormCreate;
