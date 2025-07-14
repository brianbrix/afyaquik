import {useParams} from "react-router-dom";
import {apiRequest, DetailsPage} from "@afyaquik/shared";
import ReceptionAssignmentsList from "./ReceptionAssignmentsList";
import BillingComponent from "./BillingComponent";
import {Button, Dropdown, DropdownButton} from "react-bootstrap";
import React, {useState} from "react";

const VisitActionButtons = function (visitId:any){
    const [status, setStatus] = useState<string>("");

    const updateStatus = (newStatus: string) => {
        setStatus(newStatus);
        apiRequest(`/patient/visits/status-update/${visitId}?status=${newStatus}`
        ,
            {method: 'PATCH'}
        )
            .then(() => {
                alert(`Visit status updated to ${newStatus}`);
                window.location.reload();
            })
            .catch(err => {
                console.error("Error updating status:", err);
                alert("Failed to update status. Please try again.");
            });
    };

    return (
        <div className="d-flex justify-content-between align-items-center">
            <div>
                <Button
                    variant="outline-info"
                    onClick={() => window.location.href = `index.html#/visits`}
                    className="me-2"
                >
                    Got to Visits
                </Button>
                <Button
                    variant="outline-success"
                    onClick={() => window.location.href = `index.html#/patients`}
                    className="me-2"
                >
                    Go to Patients List
                </Button>
            </div>
            <div>
                <DropdownButton id="status-dropdown" title="Update Status" variant="primary">
                    <Dropdown.Item onClick={() => updateStatus("COMPLETED")}>COMPLETED</Dropdown.Item>
                    <Dropdown.Item onClick={() => updateStatus("CANCELLED")}>CANCELLED</Dropdown.Item>
                    <Dropdown.Item onClick={() => updateStatus("IN_PROGRESS")}>IN_PROGRESS</Dropdown.Item>
                    <Dropdown.Item onClick={() => updateStatus("PENDING")}>PENDING</Dropdown.Item>
                </DropdownButton>
            </div>
        </div>
    )
}
const PatientVisitDetailsPage = () => {
    let  params = useParams();
    const patientId = useState(0);
    const id = Number(params.id);
    console.log("Visit ID", id)
    const endpoint = `/patient/visits/${id}`;
    const fields=[
        { label: "Patient Name", accessor: "patientName" },
        { label: "Visit Type", accessor: "visitType" },
        {label: "Visit Date", accessor: "visitDate", type:'datetime'},
        {label: "Reason for Visit", accessor: "summaryReasonForVisit", type:'wysiwyg'},
        {label: "Next Visit Date", accessor: "nextVisitDate" , type:'datetime'},
        {label: "Visit Status",accessor: "visitStatus"}
    ]

    return (
        <DetailsPage topComponents={[VisitActionButtons(id)]} title={"Patient visit details"} endpoint={endpoint} fields={fields}
                     otherComponentsToRender={[
                         {title:'Assignment',content:<ReceptionAssignmentsList/>},
                         {title:'Billing',content:<BillingComponent visitId={id}/>}
                     ]}
        />
    )
}
export default PatientVisitDetailsPage;
