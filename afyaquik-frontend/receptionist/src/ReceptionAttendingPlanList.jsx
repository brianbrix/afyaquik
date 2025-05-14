import {AttendingPlanList} from "@afyaquik/shared";
import {useParams} from "react-router-dom";


const ReceptionAttendingPlanList = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    return (
        <AttendingPlanList visitId={id}/>
    )
}
export default ReceptionAttendingPlanList
