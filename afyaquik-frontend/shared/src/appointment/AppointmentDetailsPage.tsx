import {Button} from "react-bootstrap";
import React from "react";
import {DetailsPage} from "../index";
interface AppointmentDetailsPageProps
{
    appointmentId: number;
}
const AppointmentDetailsPage: React.FC<AppointmentDetailsPageProps>= ({appointmentId}) => {
    const endpoint = `/appointments/${appointmentId}`;
    const back = function (){
        return (  <Button
            variant="outline-primary"
            className="btn btn-success mb-4"
            onClick={() => window.location.href = "index.html#/appointments"}
        >
            <i className="bi bi-arrow-left me-1"></i> Back To Appointment List
        </Button>)
    }

    const editButton = function (){
        return (  <Button
            variant="outline-warning"
            className="btn btn-secondary mb-4"
            onClick={() => window.location.href = `index.html#/appointments/${appointmentId}/edit`}
        >
            <i className="bi bi-pencil-fill me-1"></i> Edit Appointment
        </Button>)
    }

    const fields=[
        { label: "Date scheduled", accessor: "appointmentDateTime", type:'datetime' },
        { label: "Status", accessor: "status" },
        { label: "Reason", accessor: "reason" },
        { label: "First Name", accessor: "patient.firstName" },
        { label: "Last Name", accessor: "patient.lastName" },
        {label: "Email", accessor: "patient.contactInfo.email"},
        { label: "Phone", accessor: "patient.contactInfo.phoneNumber" },
        {label: "Gender", accessor: "patient.gender"},
        {label: "Doctor", accessor: "doctor.username"},

    ]
    const topComponents = [back()]

    return (
        <DetailsPage title={"Appointment Details"} endpoint={endpoint} fields={fields} topComponents={topComponents}
        otherComponentsToRender={[editButton()]}
        />
    )
}
export default AppointmentDetailsPage;
