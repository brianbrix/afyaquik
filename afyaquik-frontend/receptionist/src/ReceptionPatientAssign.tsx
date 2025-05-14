import {PatientAssign} from "@afyaquik/shared";
import {useParams} from "react-router-dom";


const ReceptionPatientAssign = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    return (
        <PatientAssign visitId={id}/>
    )
}
export default ReceptionPatientAssign;
