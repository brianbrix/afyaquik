import {useParams} from "react-router-dom";
import {DetailsPage, PatientAssign, PatientVisitList} from "@afyaquik/shared";
import ReceptionPatientVisitList from "./ReceptionPatientVisitList";
import ReceptionAttendingPlanList from "./ReceptionAttendingPlanList";

const PatientVisitDetailsPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    const endpoint = `/patient/visits/${id}`;
    const fields=[
        { label: "Patient Name", accessor: "patientName" },
        { label: "Visit Type", accessor: "visitType" },
        {label: "Visit Date", accessor: "visitDate"},
        {label: "Reason for Visit", accessor: "summaryReasonForVisit"},
        {label: "Next Visit Date", accessor: "nextVisitDate"}
    ]

    return (
        <DetailsPage endpoint={endpoint} fields={fields}
                     listRender={<ReceptionAttendingPlanList/>}
        />
    )
}
export default PatientVisitDetailsPage;
