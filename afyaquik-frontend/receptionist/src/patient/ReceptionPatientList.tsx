import {PatientList} from "@afyaquik/shared";
import {useParams} from "react-router-dom";


const ReceptionPatientList = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    return (
        <PatientList/>
    )
}
export default ReceptionPatientList;
