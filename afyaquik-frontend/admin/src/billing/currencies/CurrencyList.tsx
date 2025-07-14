import { List, Datagrid, TextField, BooleanField, Button } from 'react-admin';
import { useRecordContext } from 'react-admin';
import { useState } from 'react';

const ActivateButton = () => {
    const record = useRecordContext();
    const [loading, setLoading] = useState(false);

    if (!record || record.active) return null;

    const handleActivate = async () => {
        setLoading(true);
        try {
            await fetch(`/api/currencies/${record.id}/activate`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            // Refresh the list
            window.location.reload();
        } catch (error) {
            console.error('Error activating currency:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Button
            label="Set Active"
            onClick={handleActivate}
            disabled={loading}
        />
    );
};

const CurrencyList = () => (
    <List resource="currencies" pagination={false}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="name" />
            <TextField source="code" />
            <TextField source="symbol" />
            <BooleanField source="active" />
            <ActivateButton />
        </Datagrid>
    </List>
);

export default CurrencyList;
