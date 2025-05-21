import {apiRequest, StepConfig, StepForm, useToast} from "@afyaquik/shared";
import {Button} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";

export const backtoList = function (){
    return (  <Button
        variant="outline-info"
        className="btn btn-success mb-4"
        onClick={() => window.location.href = "index.html#/appointments"}
    >
        <i className="bi bi-arrow-left me-1"></i> Back To List
    </Button>)
}

const AppointmentEditForm = () => {
    const [doctors, setDoctors] = useState<{ label: string; value: number }[]>([]);
    const [selectedDoctor, setSelectedDoctor] = useState<number | undefined>(undefined);

    let  params = useParams();
    const id = params.id;
    console.log('Appointment id', id)
    const [defaultValues, setDefaultValues] = useState({});

    useEffect(() => {
        apiRequest(`/appointments/${id}`, { method: 'GET' })
            .then(data => {
                const mappedData={
                    id: data.id,
                    reason: data.reason,
                    status: data.status,
                    appointmentDateTime: data.appointmentDateTime,
                    doctorId: data.doctor.id,
                    patientId: data.patient.id,
                    firstName: data.patient.firstName,
                    secondName: data.patient.secondName,
                };
                console.log("Mapped data",mappedData)
                setDefaultValues(mappedData);
            })
            .catch(err => console.error(err));
    }, []);

    const { showToast } = useToast();

    useEffect(() => {
        apiRequest("/search", { method: "POST",
            body: {
                "searchEntity": "users",
                "query": "DOCTOR",
                "searchFields": ["roles.name"],
                "page": 0,
                "size": 100
            }
        })
            .then((data) => {
                const doctorsOptions = data.results.content.map((s: any) => ({ label: s.username, value: s.id }));
                setDoctors(doctorsOptions);
            })
            .catch(console.error);
    }, [selectedDoctor]);

    const formConfig: StepConfig[] = [
        {
            label: 'Edit Appointment',
            fields: [
                { name: 'patientId', label: 'Patient Id', type: 'text', disabled: true, colSpan:6 },
                { name: 'firstName', label: 'First Name', type: 'text', disabled: true, colSpan:6 },
                { name: 'secondName', label: 'Second Name', type: 'text', disabled: true,colSpan:6  },
                { name: 'reason', label: 'Appointment reason', type: 'text', required:true,colSpan:6  },
                {name: 'status',label: 'Status',type: 'select',options:
                        [{label: 'PENDING', value: 'PENDING'}, {label: 'COMPLETED', value: 'COMPLETED'}
                        , {label: 'CANCELLED', value: 'CANCELLED'}
                    ],colSpan:6 },
                { name: 'appointmentDateTime', label: 'Appointment Date and Time', type: 'datetime',required:true,colSpan:6  },
                {
                    name: "doctorId",
                    label: "Doctor",
                    type: "select",
                    disabled: true,
                    options: doctors, colSpan:6 ,
                    onChange: (val: number) => {
                        setSelectedDoctor(val);
                    },
                    required:true
                }
            ],
            topComponents: [backtoList()]
        }
    ];
    return (

        <StepForm
            config={formConfig}
            onSubmit={(data,) => {
                console.log('Submitted data:', data);
                apiRequest(`/appointments`, { method:'PUT' , body: data}, showToast)
                    .then(response => {
                        console.log(response)
                        window.location.href = `index.html#/patients/${response.id}/details`;
                    })
                    .catch(err => console.error(err));
            }}
            defaultValues={defaultValues}
            submitButtonLabel={'Update appointment'}
        />);
}
export default AppointmentEditForm;
