import {AttendingPlanList} from "@afyaquik/shared";
import {useParams} from "react-router-dom";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Attending Officer', accessor: 'attendingOfficerUserName' },
    { header: 'Assigned Officer', accessor: 'assignedOfficer' },
    { header: 'Next Station', accessor: 'nextStation' }
];
const ReceptionAttendingPlanList = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    return (
        <AttendingPlanList visitId={id} columns={columns} addView={"justAdd"}/>
    )
}
export default ReceptionAttendingPlanList
