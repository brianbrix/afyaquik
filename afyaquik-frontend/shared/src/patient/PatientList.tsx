import {useEffect, useState} from "react";
import { FieldConfig } from "../StepConfig";
import apiRequest from "../api";
import React from "react";
import DataTable from "../DataTable";


const columns = [
    { header: 'First Name', accessor: 'firstName' },
    { header: 'Last Name', accessor: 'lastName' },
    { header: 'Phone', accessor: 'contactInfo.phone' },
    { header: 'Id', accessor: 'nationalId' }
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
            editView={"index.html#/patient/#id/edit"}
            addView={"index.html#/patient/add"}
            detailsView={"index.html#/patient/#id/details"}
            searchFields={searchFields}
            searchEntity={'patients'}
            searchMethod={'POST'}
            searchEndpoint={'/search'}
        />);
}
export default PatientList;
