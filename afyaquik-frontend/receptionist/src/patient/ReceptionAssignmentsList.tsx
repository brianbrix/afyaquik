import {AssignmentsList} from "@afyaquik/shared";
import {useParams} from "react-router-dom";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Attending Officer', accessor: 'attendingOfficerUserName' },
    { header: 'Assigned Officer', accessor: 'assignedOfficer' },
    { header: 'Next Station', accessor: 'nextStation' }
];
const ReceptionAssignmentsList = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    return (
        <AssignmentsList visitId={id} columns={columns} addView={"justAdd"}/>
    )
}
export default ReceptionAssignmentsList
