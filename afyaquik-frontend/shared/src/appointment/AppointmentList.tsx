import {useState} from "react";
import { FieldConfig } from "../StepConfig";
import React from "react";
import DataTable from "../DataTable";


const columns = [
    { header: '#', accessor: 'id' },
    { header: 'First Name', accessor: 'patient.firstName' },
    { header: 'Last Name', accessor: 'patient.lastName' },
    { header: 'Doctor Name', accessor: 'doctor.username' },
    { header: 'Date scheduled', accessor: 'appointmentDateTime' , type:'datetime' },
    { header: 'Status', accessor: 'status' }
];
interface AppointmentListProps{

    patientId?:number;
}

const AppointmentList:  React.FC<AppointmentListProps> = ({patientId}) => {
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
        patientId ? (
            <DataTable
                title="Appointments List"
                columns={columns}
                data={appointments}
                editView={"index.html#/appointments/#id/edit"}
                addView={`index.html#/patients/${patientId}/appointments/add`}
                detailsView={"index.html#/appointments/#id/details"}
                searchFields={searchFields}
                searchEntity={'appointments'}
                requestMethod={'GET'}
                isSearchable={true}
                dataEndpoint={`/appointments/patient/${patientId}`}
            />
        ) : (
            <DataTable
                title="Appointments List"
                columns={columns}
                data={appointments}
                editView={"index.html#/appointments/#id/edit"}
                addView={"index.html#/patients"}
                detailsView={"index.html#/appointments/#id/details"}
                searchFields={searchFields}
                searchEntity={'appointments'}
                requestMethod={'POST'}
                isSearchable={true}
                dataEndpoint={'/search'}
            />
        )
    );
}
export default AppointmentList;
