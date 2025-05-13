import {apiRequest, StepConfig, StepForm} from "@afyaquik/shared";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";


const formConfig: StepConfig[] = [
    {
        label: 'Edit Patient Info',
        fields: [
            { name: 'id', label: 'User Id', type: 'text', disabled: true },
            { name: 'firstName', label: 'First Name', type: 'text', required: true },
            { name: 'secondName', label: 'Second Name', type: 'text' },
            { name: 'lastName', label: 'Last Name', type: 'text' },
            { name: 'gender', label: 'Gender', type: 'select', options: [
                    { label: 'Male', value: 'MALE' },
                    { label: 'Female', value: 'FEMALE' }
                ] },
            { name: 'dateOfBirth', label: 'Date of Birth', type: 'date' },
            { name: 'nationalId', label: 'National ID', type: 'text' },
            { name: 'maritalStatus', label: 'Marital Status', type: 'select', options: [
                    { label: 'Single', value: 'SINGLE' },
                    { label: 'Married', value: 'MARRIED' },
                    { label: 'Divorced', value: 'DIVORCED' },
                    { label: 'Widowed', value: 'WIDOWED' },
                    { label: 'Separated', value: 'SEPARATED' }
                ] },
        ],
        listUrl: 'index.html'
    },
    {
        label: 'Contact Info',
        fields: [
            { name: 'contactInfo.phone', label: 'Phone', type: 'text' },
            { name: 'contactInfo.phone2', label: 'Phone 2', type: 'text' },
            { name: 'contactInfo.email', label: 'Email', type: 'email' },
            { name: 'contactInfo.address', label: 'Address', type: 'text' }
        ],
        listUrl: 'index.html'
    }
];


const EditPatient = () => {
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
                // @ts-ignore
                apiRequest(`/patients/${id}/update`, { method:'PUT' ,body: data})
                    .then(response => {
                        console.log('Data to be updated',response)
                        window.location.href = `index.html#/patient/${id}/visits/create`;

                    })
                    .catch(err => console.error(err));
            }}
            defaultValues={defaultValues}
        />);
}
export default EditPatient;
