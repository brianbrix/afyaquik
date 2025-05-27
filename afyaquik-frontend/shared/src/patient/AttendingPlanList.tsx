import {useEffect, useState} from "react";
import apiRequest from "../api";
import React from "react";
import DataTable from "../DataTable";

interface AttendingPlanListProps {
    visitId: number;
}
const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Attending Officer', accessor: 'attendingOfficerUserName' },
    { header: 'Assigned Officer', accessor: 'assignedOfficer' },
    { header: 'Next Station', accessor: 'nextStation' }
];

const AttendingPlanList: React.FC<AttendingPlanListProps>  = ({visitId}) => {
    const [plans, setPlans] = useState([]);
    useEffect(() => {
        apiRequest(`/patient/visits/${visitId}/plan`, { method: 'GET' })
            .then(data => {
                setPlans(data);
            })
            .catch(err => console.error(err));
    }, []);


    return (

        <DataTable
            title="Attending Plan"
            columns={columns}
            data={plans}
            addView={`index.html#/patients/visits/${visitId}/assign`}
            // detailsView={"index.html#/patients/visits/#id/details"}
            isSearchable={false}
            // searchFields={searchFields}
            // searchEntity={'patients'}
            requestMethod={'GET'}
            dataEndpoint={`/patient/visits/${visitId}/plan`}
        />);
}
export default AttendingPlanList;
