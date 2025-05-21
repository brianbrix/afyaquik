import {useParams} from "react-router-dom";
import {DetailsPage} from "@afyaquik/shared";
import ReceptionAttendingPlanList from "./ReceptionAttendingPlanList";
import {Button} from "react-bootstrap";
import React from "react";


const components = function (visitId:any){
    return (
        <div className="d-flex justify-content-between">
            <Button
                variant="outline-info"
                onClick={() => window.location.href = `index.html#/patients/visits/${visitId}/details`}
            >
                Got to Visits
            </Button>
            <Button
                variant="outline-success"
                onClick={() => window.location.href = `index.html`}
            >
                Go to Patients List
            </Button>
        </div>
    )
}
const PatientVisitDetailsPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    const endpoint = `/patient/visits/${id}`;
    const fields=[
        { label: "Patient Name", accessor: "patientName" },
        { label: "Visit Type", accessor: "visitType" },
        {label: "Visit Date", accessor: "visitDate", type:'datetime'},
        {label: "Reason for Visit", accessor: "summaryReasonForVisit", type:'wysiwyg'},
        {label: "Next Visit Date", accessor: "nextVisitDate" , type:'datetime'}
    ]

    return (
        <DetailsPage topComponents={[components(id)]} title={"Patient visit details"} endpoint={endpoint} fields={fields}
                     otherComponentsToRender={[<ReceptionAttendingPlanList/>]}
        />
    )
}
export default PatientVisitDetailsPage;
