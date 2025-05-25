import {PatientVisitList} from "@afyaquik/shared";
import {useParams} from "react-router-dom";


const ReceptionPatientVisitList = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Patient ID", id)
    return (
        <PatientVisitList patientId={id}/>
    )
}
export default ReceptionPatientVisitList;
