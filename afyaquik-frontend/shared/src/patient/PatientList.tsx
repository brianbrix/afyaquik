import {useEffect, useState} from "react";
import { FieldConfig } from "../StepConfig";
import apiRequest from "../api";
import React from "react";
import DataTable from "../DataTable";
import {usePreviousLocation} from "../usePreviousLocation";
import {useToast} from "../ToastContext";


const columns = [
    { header: '#', accessor: 'id' },
    { header: 'First Name', accessor: 'firstName' },
    { header: 'Last Name', accessor: 'lastName' },
    { header: 'When Registered', accessor: 'createdAt' , type:'datetime'},
    { header: 'Phone', accessor: 'contactInfo.phone' }
];

const PatientList = () => {
    const { showToast } = useToast()

    const [patients, setPatients] = useState([]);
    useEffect(() => {
        apiRequest("/patients/search", { method: 'POST', body: {} })
            .then(data => {
                setPatients(data);
            })
            .catch(err => console.error(err));
        showToast('Search for patients or add a new patient to create appointment or add visit', 'warning');
    }, []);
    const searchFields:FieldConfig[] =[
        {
            name: "firstName",
            label: "First Name",
        },
        {
            name: "lastName",
            label: "Last Name",
        },
        {
            name: "secondName",
            label: "Second Name",
        },
        {
            name: "nationalId",
            label: "National ID",
        }

    ]

    return (

        <DataTable
            title="Patient List"
            columns={columns}
            data={patients}
            editView={"index.html#/patients/#id/edit"}
            addView={"index.html#/patients/add"}
            detailsView={"index.html#/patients/#id/details"}
            searchFields={searchFields}
            searchEntity={'patients'}
            requestMethod={'POST'}
            isSearchable={true}
            dataEndpoint={'/search'}
        />);
}
export default PatientList;
