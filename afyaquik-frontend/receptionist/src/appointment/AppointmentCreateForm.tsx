import {apiRequest, AppointmentList, StepConfig, StepForm, useToast} from "@afyaquik/shared";
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

const AppointmentCreateForm = () => {
    const [doctors, setDoctors] = useState<{ label: string; value: number }[]>([]);
    const [selectedDoctor, setSelectedDoctor] = useState<number | undefined>(undefined);
    const [doctorAppointments, setDoctorAppointments]=useState<{}|undefined>(undefined);

    let  params = useParams();
    const id = params.id;
    console.log('Patient id', id)
    const [defaultValues, setDefaultValues] = useState({});

    useEffect(() => {
        apiRequest(`/patients/${id}`, { method: 'GET' })
            .then(data => {
                const mappedData={
                    patientId: data.id,
                    firstName: data.firstName,
                    secondName: data.secondName,
                };
                console.log("Mapped data",mappedData)
                setDefaultValues(mappedData);
            })
            .catch(err => console.error(err));
    }, []);
    useEffect(() => {
        if (selectedDoctor === undefined) {
            return;
        }
        apiRequest(`/appointments/doctor/${selectedDoctor}`, { method: 'GET' })
            .then(data => {
                setDoctorAppointments(data?.results?.content);
            })
            .catch(err => console.error(err));
    }, [selectedDoctor]);

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
    }, []);

    const formConfig: StepConfig[] = [
        {
            label: 'New Appointment',
            fields: [
                { name: 'patientId', label: 'Patient Id', type: 'text', disabled: true, colSpan:6 },
                { name: 'firstName', label: 'First Name', type: 'text', disabled: true, colSpan:6 },
                { name: 'secondName', label: 'Second Name', type: 'text', disabled: true,colSpan:6  },
                { name: 'reason', label: 'Appointment reason', type: 'text', required:true,colSpan:6  },
                { name: 'appointmentDateTime', label: 'Appointment Date and Time', type: 'datetime',required:true,colSpan:6  },
                {
                    name: "doctorId",
                    label: "Doctor",
                    type: "select",
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

        <><StepForm
            config={formConfig}
            onSubmit={(data) => {
                console.log('Submitted data:', data);
                apiRequest(`/appointments`, {method: 'POST', body: data}, showToast)
                    .then(response => {
                        console.log(response);
                        window.location.href = `index.html#/appointments/${response.id}/details`;
                    })
                    .catch(err => console.error(err));
            }}
            defaultValues={defaultValues}
            submitButtonLabel={'Create appointment'}/>

            {selectedDoctor && doctorAppointments && (
                <AppointmentList data={doctorAppointments} title={"Selected doctor's existing appointments"} />
            )}
        </>

    );
}
export default AppointmentCreateForm;
