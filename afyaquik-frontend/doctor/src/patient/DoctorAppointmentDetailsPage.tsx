import {useParams} from "react-router-dom";
import {AppointmentDetailsPage, DetailsPage} from "@afyaquik/shared";

import React from "react";

const DoctorAppointmentDetailsPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Patient ID", id)

    return (
        <AppointmentDetailsPage appointmentId={id}/>
    )
}
export default DoctorAppointmentDetailsPage;
