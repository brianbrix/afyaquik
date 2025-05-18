import {useEffect, useState} from "react";
import apiRequest from "../api";
import React from "react";
import DataTable from "../DataTable";
import {useParams} from "react-router-dom";

interface PatientVisitListProps {
    patientId: number;
}
const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Visit Type', accessor: 'visitType' },
    { header: 'Date of Visit', accessor: 'visitDate' }
];



const PatientVisitList: React.FC<PatientVisitListProps> = ({patientId}) => {
    // let  params = useParams();
    // const id = Number(params.id);
    const [patientVisits, setPatientVisits] = useState([]);
    useEffect(() => {
        apiRequest(`/patients/${patientId}/visits`, { method: 'GET' })
            .then(data => {
                setPatientVisits(data);
            })
            .catch(err => console.error(err));
    }, []);
    const patientName = localStorage.getItem('patientName');
    const searchFields =[
        {
            name: 'createdAt',
            label: 'When added',
        },
        {
            name: 'visitType',
            label: 'Visit Type',
        },

        {
            name: 'visitStatus',
            label: 'Visit Status',
        }
        ,
        {
            name: 'visitDate',
            label: 'Visit Date',
        }
    ]

    return (

        <DataTable
            title={`Visits by ${patientName}`}
            columns={columns}
            data={patientVisits}
            editView={"index.html#/patient/visits/#id/edit"}
            addView={`index.html#/patient/${patientId}/visits/add`}
            searchFields={searchFields}
            // searchEntity={'visits'}
            requestMethod={'GET'}
            dataEndpoint={`/patients/${patientId}/visits`}
        />);
}
export default PatientVisitList;
