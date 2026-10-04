import {useLocation, useParams} from "react-router-dom";
import {DetailsPage, EncounterOperations} from "@afyaquik/shared";
import ReceptionAssignmentsList from "./ReceptionAssignmentsList";
import BillingComponent from "./BillingComponent";
import React from "react";
const PatientVisitDetailsPage = () => {
    let  params = useParams();
    const location = useLocation();
    const id = Number(params.id);
    const endpoint = `/patient/visits/${id}`;
    const fields=[
        { label: "Patient Name", accessor: "patientName" },
        { label: "Visit Type", accessor: "visitType" },
        {label: "Visit Date", accessor: "visitDate", type:'datetime'},
        {label: "Reason for Visit", accessor: "summaryReasonForVisit", type:'wysiwyg'},
        {label: "Next Visit Date", accessor: "nextVisitDate" , type:'datetime'},
        {label: "Visit Status",accessor: "visitStatus"}
    ]

    const searchParams = new URLSearchParams(location.search);
    const tabParam = searchParams.get('tab');


    return (
        <DetailsPage title={"Patient visit details"} endpoint={endpoint} fields={fields} activeTab={tabParam||undefined}
                     otherComponentsToRender={[
                         {title:'Assignment',content:<ReceptionAssignmentsList/>},
                         {title:'Workflow',content:<EncounterOperations visitId={id}/>},
                         {title:'Billing',content:<BillingComponent visitId={id}/>}
                     ]}
        />
    )
}
export default PatientVisitDetailsPage;
