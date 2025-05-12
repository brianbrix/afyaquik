import {apiRequest, DataTable, FieldConfig} from "@afyaquik/shared";
import {useEffect, useState} from "react";


const columns = [
    { header: 'First Name', accessor: 'firstName' },
    { header: 'Last Name', accessor: 'lastName' },
    { header: 'Phone', accessor: 'contactInfo.phone' },
    { header: 'Id', accessor: 'nationalId' }
];

const fields: FieldConfig[] = [
    { name: 'firstName', label: 'First Name', type: 'text', required: true },
    { name: 'lastName', label: 'Last Name', type: 'text', required: true },
    { name: 'contactInfo.phone', label: 'Phone', type: 'text' },
    { name: 'gender', label: 'Gender', type: 'text' },
    { name: 'dateOfBirth', label: 'DOB', type: 'text' },
];
const PatientList = () => {
    const [patients, setPatients] = useState([]);
    useEffect(() => {
        apiRequest("/patients/search", { method: 'POST', body: {} })
            .then(data => {
                setPatients(data);
            })
            .catch(err => console.error(err));
    }, []);

    return (

        <DataTable
            title="Patient List"
            columns={columns}
            data={patients}
            fields={fields}
            onAdd={(record) => console.log('Add:', record)}
            onEdit={(record) => console.log('Edit:', record)}
        />);
}
export default PatientList;
