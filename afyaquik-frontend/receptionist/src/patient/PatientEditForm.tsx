import {apiRequest, StepConfig, StepForm} from "@afyaquik/shared";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {backtoList} from "./PatientRegisterForm";
import {Form} from "react-bootstrap";


const formConfig: StepConfig[] = [
    {
        label: 'Edit Patient Info',
        fields: [
            { name: 'id', label: 'User Id', type: 'text', colSpan:6 , disabled: true },
            { name: 'firstName', label: 'First Name', type: 'text', colSpan:6 , required: true },
            { name: 'secondName', label: 'Second Name', colSpan:6 , type: 'text' },
            { name: 'lastName', label: 'Last Name', type: 'text', colSpan:6  },
            { name: 'gender', label: 'Gender', type: 'select', colSpan:6 , options: [
                    { label: 'Male', value: 'MALE' },
                    { label: 'Female', value: 'FEMALE' }
                ] },
            { name: 'dateOfBirth', label: 'Date of Birth', type: 'date', colSpan:6  },
            { name: 'nationalId', label: 'National ID', type: 'text' , colSpan:6 },
            { name: 'maritalStatus', label: 'Marital Status', type: 'select', colSpan:6 , options: [
                    { label: 'Single', value: 'SINGLE' },
                    { label: 'Married', value: 'MARRIED' },
                    { label: 'Divorced', value: 'DIVORCED' },
                    { label: 'Widowed', value: 'WIDOWED' },
                    { label: 'Separated', value: 'SEPARATED' }
                ] },
        ],
        topComponents: [backtoList()]
    },
    {
        label: 'Contact Info',
        fields: [
            { name: 'contactInfo.phoneNumber', label: 'Phone', type: 'text', colSpan:6  },
            { name: 'contactInfo.phoneNumber2', label: 'Phone 2', type: 'text', colSpan:6  },
            { name: 'contactInfo.email', label: 'Email', type: 'email', colSpan:6  },
            { name: 'contactInfo.address', label: 'Address', type: 'text', colSpan:6  }
        ],
        topComponents: [backtoList()]
    }
];


const PatientEditForm = () => {
    const [goToAppointment, setGoToAppointment] = useState(false);

    let  params = useParams();
    const id = params.id;
    console.log('id', id)
    const [defaultValues, setDefaultValues] = useState([]);
    useEffect(() => {
        apiRequest(`/patients/${id}`, { method: 'GET' })
            .then(data => {
                setDefaultValues(data);
            })
            .catch(err => console.error(err));
    }, []);
    return (

        <StepForm
            config={formConfig}
            onSubmit={(data) => {
                apiRequest(`/patients/${id}/update`, { method:'PUT' ,body: data})
                   .then(response=>{
                       if (goToAppointment && response?.id) {
                           console.log('Go to app', goToAppointment)
                           window.location.href= `index.html#/patients/${response.id}/appointments/add`;
                       } else if (response?.id) {
                           console.log('Go to details', goToAppointment)
                           window.location.href= `index.html#/patients/${response.id}/details`;
                       }
                   })
                   .catch(error=>{
                       console.error('Error:', error);
                       throw error;
                   })
            }}
            defaultValues={defaultValues}
            submitButtonLabel={'Update Patient'}
            bottomComponents={[
                <Form.Check
                    key="checkbox"
                    type="checkbox"
                    label="Go to appointment after update"
                    checked={goToAppointment}
                    onChange={(e) => setGoToAppointment(e.target.checked)}
                />
            ]}
        />);
}
export default PatientEditForm;
