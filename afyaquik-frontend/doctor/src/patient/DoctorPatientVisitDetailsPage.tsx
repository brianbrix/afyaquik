import {useParams} from "react-router-dom";
import {AttendingPlanList, DetailsPage} from "@afyaquik/shared";
import {Button} from "react-bootstrap";
import React from "react";


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
const attendingPlanList= function (visitId:number){
return (
    <AttendingPlanList visitId={visitId} columns={columns} detailsView={"index.html#/plans/#id/details"}/>
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
                         {title:'Attending Plan',content:attendingPlanList(id)}
                     ]}
        />
    )
}
export default DoctorPatientVisitDetailsPage;
