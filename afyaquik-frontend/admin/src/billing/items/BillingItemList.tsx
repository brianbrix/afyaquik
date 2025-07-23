import { List, Datagrid, TextField, NumberField, BooleanField } from 'react-admin';

const BillingItemList = () => (
    <List resource={"billingItems"} pagination={false}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <TextField source="description" />
            <NumberField source="defaultAmount" options={{ style: 'currency', currency: 'KES' }} />
            <BooleanField source="active" />
        </Datagrid>
    </List>
);

export default BillingItemList;
