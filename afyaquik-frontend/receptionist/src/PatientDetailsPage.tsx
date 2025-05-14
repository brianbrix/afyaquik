import {useParams} from "react-router-dom";
import {DetailsPage, PatientAssign, PatientVisitList} from "@afyaquik/shared";
import ReceptionPatientVisitList from "./ReceptionPatientVisitList";

const PatientDetailsPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Patient ID", id)
    const endpoint = `/patients/${id}`;
    const fields=[
        { label: "First Name", accessor: "firstName" },
        { label: "Last Name", accessor: "lastName" },
        {label: "Gender", accessor: "gender"},
        {label: "Date of Birth", accessor: "dateOfBirth"},
        {label: "National Id", accessor: "nationalId"},
        {label: "Marital Status", accessor: "maritalStatus"},
        {label: "Email", accessor: "contactInfo.email"},
        { label: "Phone", accessor: "contactInfo.phone" }
    ]

    return (
        <DetailsPage endpoint={endpoint} fields={fields}
        listRender={<ReceptionPatientVisitList/>}
        />
    )
}
export default PatientDetailsPage;
