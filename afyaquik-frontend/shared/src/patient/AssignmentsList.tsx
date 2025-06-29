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
    userId?: number;
    whichOfficer?: string; // Optional prop to specify which officer's assignments to fetch
}


const AssignmentsList: React.FC<AssignmentsListProps>  = ({visitId, columns, dataEndpoint, editView,addView,detailsView,title,userId, whichOfficer='attending'}) => {
    const [plans, setPlans] = useState([]);
    let url = `/patient/visits/${visitId}/assignments`;
    if (userId) {
        url = `/patient/visits/${visitId}/assignments/${userId}?whichOfficer=${whichOfficer}`;

    }

    useEffect(() => {
        apiRequest(dataEndpoint?dataEndpoint:url, { method: 'GET' })
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
        dataEndpoint: url
    };

    if (addView) {
        dataTableProps.addView = `index.html#/patients/visits/${visitId}/assign`;
        dataTableProps.addTitle = 'Add Assignment';

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
