import {useParams} from "react-router-dom";
import {AppointmentList} from "@afyaquik/shared";

const ReceptionPatientAppointmentList = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Patient ID", id)
    return (
        <AppointmentList patientId={id}/>
    )
}
export default ReceptionPatientAppointmentList;
