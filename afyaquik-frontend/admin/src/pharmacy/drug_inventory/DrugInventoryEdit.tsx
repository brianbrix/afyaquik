import { Edit, SimpleForm, TextInput, NumberInput, DateInput, DateTimeInput, BooleanInput, useRecordContext, Labeled } from 'react-admin';

const validatePositive = (value: number) => {
    if (value < 0) {
        return 'Value must be positive';
    }
};

const LockedMessage = () => {
    const record = useRecordContext();
    if (!record || !record.locked) return null;

    return (
        <div style={{ color: 'red', padding: '15px', border: '1px solid red', marginBottom: '20px' }}>
            This inventory record cannot be altered because it is locked.
        </div>
    );
};

const DrugInventoryEdit = () => {
    const record = useRecordContext();
    const isLocked = record?.locked;

    return (
        <Edit resource={"drugInventory"}>
            <SimpleForm>
                <LockedMessage />
                <TextInput source="id" disabled={true} />
                <TextInput name="drugName" source="drugName" label="Drug Name" disabled={isLocked} />
                <NumberInput source="initialQuantity" label="Initial Quantity" disabled={true} />
                <NumberInput source="currentQuantity" label="Current Quantity" disabled={true} />

                <TextInput source="batchNumber" label="Batch Number" disabled={isLocked} />
                <DateInput source="expiryDate" label="Expiry Date" required={true} disabled={isLocked} />
                <DateTimeInput source="receivedDate" label="Received Date" disabled={isLocked} />

                <NumberInput
                    source="sellingPrice"
                    label="Selling Price"
                    validate={[validatePositive]}
                    disabled={isLocked}
                />

                <NumberInput
                    source="buyingPrice"
                    label="Buying Price"
                    validate={[validatePositive]}
                    disabled={isLocked}
                />

                <BooleanInput source="active" label="Is Active" defaultValue={true} disabled={isLocked} />
                <BooleanInput source="locked" label="Is Locked" defaultValue={false} disabled={isLocked} />

                <TextInput source="supplierName" label="Supplier Name" disabled={isLocked} />
                <TextInput source="comment" label="Comments" multiline={true} disabled={isLocked} />
            </SimpleForm>
        </Edit>
    );
};

export default DrugInventoryEdit;
