import {useParams} from "react-router-dom";
import {DetailsPage} from "@afyaquik/shared";
import {Button} from "react-bootstrap";
import React from "react";
import ReceptionAppointmentList from "../appointment/ReceptionAppointmentList";

const AppointmentDetailsPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Patient ID", id)
    const endpoint = `/appointments/${id}`;
    const back = function (){
        return (  <Button
            variant="outline-primary"
            className="btn btn-success mb-4"
            onClick={() => window.location.href = "index.html#/appointments"}
        >
            <i className="bi bi-arrow-left me-1"></i> Back To Appointment List
        </Button>)
    }

    const fields=[
        { label: "Date scheduled", accessor: "appointmentDateTime" },
        { label: "Status", accessor: "status" },
        { label: "Reason", accessor: "reason" },
        { label: "First Name", accessor: "patient.firstName" },
        { label: "Last Name", accessor: "patient.lastName" },
        {label: "Email", accessor: "patient.contactInfo.email"},
        { label: "Phone", accessor: "patient.contactInfo.phone" },
        {label: "Gender", accessor: "patient.gender"},
        {label: "Doctor", accessor: "doctor.username"},

    ]
    const topComponents = [back()]

    return (
        <DetailsPage title={"Appointment Details"} endpoint={endpoint} fields={fields} topComponents={topComponents}

        />
    )
}
export default AppointmentDetailsPage;
