import {apiRequest, StepConfig, StepForm} from "@afyaquik/shared";
import {Button} from "react-bootstrap";
import React from "react";

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
const RegisterPatient = () => {

    return (

        <StepForm
            config={formConfig}
            onSubmit={(data,) => {
                console.log('Submitted data:', data);
                apiRequest(`/patients`, { method:'POST' , body: data})
                    .then(response => {
                        console.log(response)
                        window.location.href = `index.html#/patient/${response.id}/details`;

                    })
                    .catch(err => console.error(err));
            }}
            defaultValues={{}}
            submitButtonLabel={'Register Patient'}
        />);
}
export default RegisterPatient;
