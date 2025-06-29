import {useParams} from "react-router-dom";
import TreatmentPlanAddPage from "./TreatmentPlanAddPage";

const DoctorTreatmentPlanAddPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    return (
        <TreatmentPlanAddPage planId={id} />
    );
}
export default DoctorTreatmentPlanAddPage;
