import {useParams} from "react-router-dom";
import {DetailsPage} from "@afyaquik/shared";
import ReceptionPatientVisitList from "./ReceptionPatientVisitList";
import {Button} from "react-bootstrap";
import React from "react";
import ReceptionPatientAppointmentList from "../appointment/ReceptionAppointmentList";

const PatientDetailsPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Patient ID", id)
    const endpoint = `/patients/${id}`;
    const back = function (){
        return (  <Button
            variant="outline-primary"
            className="btn btn-success mb-4"
            onClick={() => window.location.href = "index.html"}
        >
            <i className="bi bi-arrow-left me-1"></i> Back To Patient List
        </Button>)
    }

    const fields=[
        { label: "First Name", accessor: "firstName" },
        { label: "Last Name", accessor: "lastName" },
        {label: "Gender", accessor: "gender"},
        {label: "Date of Birth", accessor: "dateOfBirth", type:'datetime'},
        {label: "National Id", accessor: "nationalId"},
        {label: "Marital Status", accessor: "maritalStatus"},
        {label: "Email", accessor: "contactInfo.email"},
        { label: "Phone", accessor: "contactInfo.phoneNumber" }
    ]
    const topComponents = [back()]

    return (
        <DetailsPage title={"Patient Details"} endpoint={endpoint} fields={fields} topComponents={topComponents}
        otherComponentsToRender={[<ReceptionPatientVisitList/>,<ReceptionPatientAppointmentList/>]}
        />
    )
}
export default PatientDetailsPage;
