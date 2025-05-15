import {useEffect, useState} from "react";
import { FieldConfig } from "../StepConfig";
import apiRequest from "../api";
import React from "react";
import DataTable from "../DataTable";

interface AttendingPlanListProps {
    visitId: number;
}
const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Attending Officer', accessor: 'attendingOfficer' },
    { header: 'Assigned Officer', accessor: 'assignedOfficer' },
    { header: 'Next Station', accessor: 'nextStation' }
];

const AttendingPlanList: React.FC<AttendingPlanListProps>  = ({visitId}) => {
    const [plans, setPlans] = useState([]);
    useEffect(() => {
        apiRequest(`/patient/visits/${visitId}?detailsType=attendingPlan`, { method: 'GET' })
            .then(data => {
                setPlans(data);
            })
            .catch(err => console.error(err));
    }, []);
    const searchFields:FieldConfig[] =[
        {
            name: "attendingOfficerUserName",
            label: "Attending Officer",
        },
        {
            name: "assignedOfficer",
            label: "Assigned Officer",
        },
        {
            name: "nextStation",
            label: "Next Station",
        }

    ]

    return (

        <DataTable
            title="Attending Plan"
            columns={columns}
            data={plans}
            addView={`index.html#/patient/visits/${visitId}/assign`}
            detailsView={"index.html#/patient/#id/details"}
            // searchFields={searchFields}
            // searchEntity={'patients'}
            // searchMethod={'POST'}
            // searchEndpoint={'/search'}
        />);
}
export default AttendingPlanList;
