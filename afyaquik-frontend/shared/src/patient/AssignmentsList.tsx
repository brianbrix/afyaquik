import {useEffect, useState} from "react";
import apiRequest from "../api";
import React from "react";
import DataTable from "../DataTable";

interface AssignmentsListProps {
    visitId: number;
    columns: { header: string; accessor: string; sortable?: boolean | undefined; type?: string | undefined; }[];
    dataEndpoint?:string;
    editView?: string;
    addView?: string;
    detailsView?: string;
    title?: string;
}


const AssignmentsList: React.FC<AssignmentsListProps>  = ({visitId, columns, dataEndpoint, editView,addView,detailsView,title}) => {
    const [plans, setPlans] = useState([]);
    useEffect(() => {
        apiRequest(dataEndpoint?dataEndpoint:`/patient/visits/${visitId}/assignments`, { method: 'GET' })
            .then(data => {
                setPlans(data);
            })
            .catch(err => console.error(err));
    }, []);

    const dataTableProps: any = {
        title: title?`${title}`:"Assignment" ,
        columns,
        data: plans,
        isSearchable: false,
        requestMethod: 'GET',
        dataEndpoint: `/patient/visits/${visitId}/assignments`
    };

    if (addView) {
        dataTableProps.addView = `index.html#/patients/visits/${visitId}/assign`;
    }

    if (editView) {
        dataTableProps.editView = editView;
        dataTableProps.editTitle= 'Add Observation';
        dataTableProps.editClassName= 'bi bi-plus-circle me-1';
    }

    if (detailsView) {
        dataTableProps.detailsView = detailsView;
        dataTableProps.detailsTitle= 'Add Treatment Plan';
        dataTableProps.detailsClassName= 'bi bi-plus-square me-1';
    }

    return <DataTable {...dataTableProps} />;
}
export default AssignmentsList;
