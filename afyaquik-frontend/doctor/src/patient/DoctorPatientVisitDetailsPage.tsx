import {useParams} from "react-router-dom";
import {AssignmentsList, DetailsPage} from "@afyaquik/shared";
import {Button} from "react-bootstrap";
import React from "react";
import DoctorObservationReportList from "./DoctorObservationReportList";
import DoctorTreatmentPlanListPage from "./DoctorTreatmentPlanListPage";


const components = function (visitId:any){
    return (
        <div className="d-flex justify-content-between">
            <Button
                variant="outline-info"
                onClick={() => window.location.href = `index.html`}
            >
                Got to Visits
            </Button>
        </div>
    )
}
const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Assigning Officer', accessor: 'attendingOfficerUserName' },
    { header: 'Assigned Date/Time', accessor: 'updatedAt', type:'datetime' },
    { header: 'Station', accessor: 'nextStation' }
];
const assignmentList= function (visitId:number){
    const userId = Number(localStorage.getItem('userId'));
    if (!userId) {
        console.error("Please log in to view assignments.");
        return (<div>
            <p>Please log in to view assignments.</p>
        </div>)
    }
    return (
    <AssignmentsList title={'My assignments for this patient visit'} visitId={visitId} userId={userId} whichOfficer={'assigned'} columns={columns} editView={"index.html#/assignments/#id/edit"} detailsView={"index.html#/visits/#id/treatment/plans/create"}/>
)


}
const observationReportsList= function (visitId:number){
    return (
        <DoctorObservationReportList visitId={visitId} />
    )
}

const treatmentPlanReportList= function (visitId:number){
    return (
        <DoctorTreatmentPlanListPage visitId={visitId} />
    )
}
const DoctorPatientVisitDetailsPage = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log("Visit ID", id)
    const endpoint = `/patient/visits/${id}`;
    const fields=[
        { label: "Patient Name", accessor: "patientName" },
        { label: "Visit Type", accessor: "visitType" },
        {label: "Visit Date", accessor: "visitDate", type:'datetime'},
        {label: "Reason for Visit", accessor: "summaryReasonForVisit", type:'wysiwyg'},
        {label: "Next Visit Date", accessor: "nextVisitDate" , type:'datetime'}
    ]

    return (
        <DetailsPage topComponents={[components(id)]} title={"Patient visit details"} endpoint={endpoint} fields={fields}
                     otherComponentsToRender={[
                         {title:'Assignments',content:assignmentList(id)},
                         {title:'Observation Reports',content:observationReportsList(id)},
                         {title:'Treatment Plan',content:treatmentPlanReportList(id)},
                     ]}
        />
    )
}
export default DoctorPatientVisitDetailsPage;
