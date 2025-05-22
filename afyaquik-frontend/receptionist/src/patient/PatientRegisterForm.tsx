import {apiRequest, StepConfig, StepForm} from "@afyaquik/shared";
import {Button, Form} from "react-bootstrap";
import React, {useState} from "react";

export const backtoList = function (){
    return (  <Button
        variant="outline-info"
        className="btn btn-success mb-4"
        onClick={() => window.location.href = "index.html"}
    >
        <i className="bi bi-arrow-left me-1"></i> Back To List
    </Button>)
}
const formConfig: StepConfig[] = [
    {
        label: 'New Patient Info',
        fields: [
            { name: 'firstName', label: 'First Name', type: 'text', required: true, colSpan:6 },
            { name: 'secondName', label: 'Second Name', type: 'text', colSpan:6  },
            { name: 'lastName', label: 'Last Name', type: 'text', colSpan:6  },
            { name: 'gender', label: 'Gender', type: 'select', colSpan:6 , options: [
                    { label: 'Male', value: 'MALE' },
                    { label: 'Female', value: 'FEMALE' }
                ] },
            { name: 'dateOfBirth', label: 'Date of Birth', type: 'date', colSpan:6  },
            { name: 'nationalId', label: 'National ID', type: 'text', colSpan:6  },
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
            { name: 'contactInfo.phone', label: 'Phone', type: 'text', colSpan:6  },
            { name: 'contactInfo.phone2', label: 'Phone 2', type: 'text', colSpan:6  },
            { name: 'contactInfo.email', label: 'Email', type: 'email' , colSpan:6 },
            { name: 'contactInfo.address', label: 'Address', type: 'text', colSpan:6  }
        ],
        topComponents: [backtoList()]
    }
];
const PatientRegisterForm = () => {
    const [goToAppointment, setGoToAppointment] = useState(false);

    return (

        <StepForm
            config={formConfig}
            onSubmit={(data,) => {
                console.log('Submitted data err:', data);
                 apiRequest(`/patients`, { method:'POST' , body: data})
                     .then((response)=>{
                         console.log('Response:', response);
                         console.log('Go to appointment', goToAppointment)
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

            defaultValues={{}}
            submitButtonLabel={'Register Patient'}
            bottomComponents={[
                <Form.Check
                    key="checkbox"
                    type="checkbox"
                    label="Go to appointment after registration"
                    checked={goToAppointment}
                    onChange={(e) => setGoToAppointment(e.target.checked)}
                />
            ]}
        />);
}
export default PatientRegisterForm;
