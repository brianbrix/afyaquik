import {PatientAssignForm} from "@afyaquik/shared";
import {useParams} from "react-router-dom";


const ReceptionPatientAssignForm = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    return (
        <PatientAssignForm visitId={id}/>
    )
}
export default ReceptionPatientAssignForm;
