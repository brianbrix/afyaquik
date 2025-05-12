import {List, Datagrid, TextField, EditButton} from 'react-admin';

const SettingsList = () => (
    <List pagination={false}>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <TextField source="settingKey" />
            <TextField source="settingValue" />
            <EditButton />
        </Datagrid>
    </List>
);

export default SettingsList;
