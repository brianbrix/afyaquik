import {apiRequest, StepConfig, StepForm} from "@afyaquik/shared";


const formConfig: StepConfig[] = [
    {
        label: 'New Patient Info',
        fields: [
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
const RegisterPatient = () => {

    return (

        <StepForm
            config={formConfig}
            onSubmit={(data) => {
                console.log('Submitted data:', data);
                // @ts-ignore
                apiRequest(`/patients`, { method:'POST' , body: data})
                    .then(response => {
                        console.log(response)
                    })
                    .catch(err => console.error(err));
            }}
            defaultValues={{}}
        />);
}
export default RegisterPatient;
