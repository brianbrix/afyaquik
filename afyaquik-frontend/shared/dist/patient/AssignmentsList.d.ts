import React from "react";
interface AssignmentsListProps {
    visitId: number;
    columns: {
        header: string;
        accessor: string;
        sortable?: boolean | undefined;
        type?: string | undefined;
    }[];
    dataEndpoint?: string;
    editView?: string;
    editTitle?: string;
    editClassName?: string;
    editButtonAction?: (rowData: any) => void;
    addView?: string;
    detailsView?: string;
    title?: string;
    userId?: number;
    whichOfficer?: string;
}
declare const AssignmentsList: React.FC<AssignmentsListProps>;
export default AssignmentsList;
