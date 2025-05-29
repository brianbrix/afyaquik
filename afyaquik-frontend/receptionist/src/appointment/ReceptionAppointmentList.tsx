import {useParams} from "react-router-dom";
import {AppointmentList} from "@afyaquik/shared";

const ReceptionPatientAppointmentList = () => {
    let  params = useParams();
    const id = Number(params.id);
    return (
        <AppointmentList query={`patient.id=${id}`}/>
    )
}
export default ReceptionPatientAppointmentList;
