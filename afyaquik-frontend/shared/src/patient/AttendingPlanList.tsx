import {useEffect, useState} from "react";
import apiRequest from "../api";
import React from "react";
import DataTable from "../DataTable";

interface AttendingPlanListProps {
    visitId: number;
    columns: { header: string; accessor: string; sortable?: boolean | undefined; type?: string | undefined; }[];
    dataEndpoint?:string;
    editView?: string;
    addView?: string;
    detailsView?: string;
    title?: string;
}


const AttendingPlanList: React.FC<AttendingPlanListProps>  = ({visitId, columns, dataEndpoint, editView,addView,detailsView,title}) => {
    const [plans, setPlans] = useState([]);
    useEffect(() => {
        apiRequest(dataEndpoint?dataEndpoint:`/patient/visits/${visitId}/plans`, { method: 'GET' })
            .then(data => {
                setPlans(data);
            })
            .catch(err => console.error(err));
    }, []);

    const dataTableProps: any = {
        title: title?`${title}`:"Attending Plan" ,
        columns,
        data: plans,
        isSearchable: false,
        requestMethod: 'GET',
        dataEndpoint: `/patient/visits/${visitId}/plans`
    };

    if (addView) {
        dataTableProps.addView = `index.html#/patients/visits/${visitId}/assign`;
    }

    if (editView) {
        dataTableProps.editView = editView;
    }

    if (detailsView) {
        dataTableProps.detailsView = detailsView;
    }

    return <DataTable {...dataTableProps} />;
}
export default AttendingPlanList;
