import { StepConfig } from "../StepConfig";
import StepForm from "../StepForm";
import apiRequest from "../api";
import React, { useEffect, useRef, useState } from "react";
import {Button} from "react-bootstrap";
import {useToast} from "../ToastContext";
import {sendNotification} from "../communication/NotificationService";
import {portalUrl, PORTAL_ROLES} from "../session";
interface PatientAssignProps {
    visitId?: number;
}


const PatientAssignForm:React.FC<PatientAssignProps>  = ({visitId}) => {
    const [stations, setStations] = useState<{ label: string; value: string; roles: string[] }[]>([]);
    const [officers, setOfficers] = useState<{ label: string; value: string; roles: string[] }[]>([]);
    const [selectedStation, setSelectedStation] = useState('');
    const formMethods = useRef<any>(null);
    const [formValues] = useState({
        patientVisitId: visitId,
        nextStation: '',
        assignedOfficer: ''
    });
    const { showToast } = useToast();

    useEffect(() => {
        apiRequest("/stations", { method: "GET" })
            .then((data) => {
                const stationOptions = data.map((s: any) => ({ label: s.name, value: s.name, roles: s.allowedRoles || [] }));
                setStations(stationOptions);
            })
            .catch(error => showToast(error.message || 'Unable to load stations', 'error'));
    }, []);

    const back = function (){
        return (  <Button
            variant="outline-info"
            className="btn btn-success mb-4"
            onClick={() => window.location.href = `index.html#/visits/${visitId}/details`}
        >
            <i className="bi bi-arrow-left me-1"></i> Back to Summary
        </Button>)
    }
    useEffect(() => {
        let cancelled = false;
        setOfficers([]);
        if (!selectedStation) return;
        apiRequest(`/stations/${encodeURIComponent(selectedStation)}/users`, { method: "GET" })
            .then((users) => {
                if (cancelled) return;
                const officerOptions = users.map((user: any) => ({
                    label: [user.firstName, user.secondName, user.lastName].filter(Boolean).join(' ') || user.username,
                    value: user.username,
                    roles: user.roles || []
                }));
                setOfficers(officerOptions);
            })
            .catch(error => { if (!cancelled) showToast(error.message || 'Unable to load officers', 'error'); });
        return () => { cancelled = true; };
    }, [selectedStation]);


    const handleFieldChange = (fieldName: string, value: any) => {
        if (fieldName === 'nextStation') {
            setSelectedStation(value);
            formMethods.current?.setValue('assignedOfficer', '');
        }
    };

    const formConfig: StepConfig[] = [
        {
            label: "Assign Patient",
            fields: [
                {
                    name: "patientVisitId",
                    label: "Visit Identifier",
                    type: "text",
                    disabled: true, colSpan:6
                },
                {
                    name: "nextStation",
                    label: "Next Station",
                    type: "select",
                    options: stations, colSpan:6 ,
                    onChange: (val: any) => handleFieldChange('nextStation', val),
                    required:true
                },

                {
                    name: "assignedOfficer",
                    label: "Next Officer",
                    type: "select",
                    onChange: (val: any) => handleFieldChange('assignedOfficer', val),
                    options: officers, colSpan:6,required:true
                }
            ],

            topComponents:[back()]
        }
    ];

    return (
        <StepForm
            config={formConfig}
            onSubmit={async (data) => {
                const response = await apiRequest('/patient/visits/assignments/create', { method: 'POST', body: data });
                const officer = officers.find(option => option.value === data.assignedOfficer);
                const station = stations.find(option => option.value === data.nextStation);
                const role = officer?.roles.find(candidate => station?.roles.includes(candidate));
                const portal = (Object.keys(PORTAL_ROLES) as Array<keyof typeof PORTAL_ROLES>)
                    .find(name => name !== 'auth' && PORTAL_ROLES[name].some(candidate => candidate === role));
                try {
                    if (role && portal) {
                        await sendNotification(response.assignedOfficerId, 'New Patient Alert',
                            `You have been assigned a patient at ${response.nextStation}`,
                            portalUrl(portal, `/visits/${visitId}/details`), 'VISIT', role);
                    } else {
                        showToast('Handoff saved. This station has no supported notification portal.', 'warning');
                    }
                } catch {
                    showToast('Handoff saved, but the notification could not be sent.', 'warning');
                }
                window.location.href = `index.html#/visits/${visitId}/details`;
            }}
            idFromParent={visitId}
            defaultValues={formValues}
            formMethodsRef={formMethods}
            submitButtonLabel={'Assign Patient'}
        />
    );
};

export default PatientAssignForm;
