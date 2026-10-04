import {useState} from "react";
import { FieldConfig } from "../StepConfig";
import React from "react";
import DataTable from "../DataTable";


const columns = [

    { header: 'First Name', accessor: 'patient.firstName' },
    { header: 'Last Name', accessor: 'patient.lastName' },
    { header: 'Doctor Name', accessor: 'doctor.username' },
    { header: 'Date scheduled', accessor: 'appointmentDateTime' , type:'datetime' },
    { header: 'Status', accessor: 'status' }
];
interface AppointmentListProps{

    patientId?:number;
    data?:any;
    title?:string;
    query?:string;
    canEdit?:boolean;
}

const AppointmentList:  React.FC<AppointmentListProps> = ({patientId, data, title, query, canEdit = true}) => {
    const [appointments, setAppointments] = useState([]);

    const searchFields:FieldConfig[] =[
        {
            name: "patient.firstName",
            label: "First Name",
        },
        {
            name: "patient.lastName",
            label: "Last Name",
        },
        {
            name: "patient.secondName",
            label: "Second Name",
        },
        {
            name: "doctor.username",
            label: "Doctor",
        }

    ]

    return (
        data? (
            <DataTable
                title={title?title:"Appointments List"}
                columns={columns}
                data={data}
                searchEntity={'appointments'}
            />
        ):
            (

            query?(
            <DataTable
                title={title?title:"Appointments List"}
                columns={columns}
                data={appointments}
                editView={canEdit ? "index.html#/appointments/#id/edit" : undefined}
                showMultipleDeleteButton={canEdit}
                detailsView={"index.html#/appointments/#id/details"}
                searchFields={searchFields}
                searchEntity={'appointments'}
                requestMethod={'POST'}
                isSearchable={true}
                dateFieldName={'appointmentDateTime'}
                dataEndpoint={'/search'}
                combinedSearchFieldsAndTerms={query}
            />):
            (
                <DataTable
                    title={title?title:"Appointments List"}
                    columns={columns}
                    data={appointments}
                    editView={canEdit ? "index.html#/appointments/#id/edit" : undefined}
                    addView={canEdit ? "index.html#/patients" : undefined}
                    showMultipleDeleteButton={canEdit}
                    detailsView={"index.html#/appointments/#id/details"}
                    searchFields={searchFields}
                    searchEntity={'appointments'}
                    requestMethod={'POST'}
                    isSearchable={true}
                    dateFieldName={'appointmentDateTime'}
                    dataEndpoint={'/search'}
        />
    )




        )

    );
}
export default AppointmentList;
