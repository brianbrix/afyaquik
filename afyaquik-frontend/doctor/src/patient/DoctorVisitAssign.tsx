import {PatientAssignForm} from "@afyaquik/shared";
import {useParams} from "react-router-dom";


const DoctorVisitAssign = () => {
    let  params = useParams();
    const id = Number(params.id);
    return (
        <PatientAssignForm visitId={id}/>
    )
}
export default DoctorVisitAssign;
