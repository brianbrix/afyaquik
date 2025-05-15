import { StepConfig } from "../StepConfig";
import StepForm from "../StepForm";
import apiRequest from "../api";
import React, { useEffect, useState } from "react";
import {Button} from "react-bootstrap";
interface PatientAssignProps {
    visitId?: number;
}


const PatientAssign:React.FC<PatientAssignProps>  = ({visitId}) => {
    const [stations, setStations] = useState<{ label: string; value: string }[]>([]);
    const [officers, setOfficers] = useState<{ label: string; value: string }[]>([]);
    const [selectedStation, setSelectedStation] = useState<string | undefined>(undefined);

    useEffect(() => {
        apiRequest("/stations", { method: "GET" })
            .then((data) => {
                const stationOptions = data.map((s: any) => ({ label: s.name, value: s.id }));
                setStations(stationOptions);
            })
            .catch(console.error);
    }, []);

    const back = function (){
        return (  <Button
            variant="outline-info"
            className="btn btn-success mb-4"
            onClick={() => window.location.href = `index.html#/patient/visits/${visitId}/edit`}
        >
            <i className="bi bi-arrow-left me-1"></i> Back to Summary
        </Button>)
    }
    // Fetch officers when station changes
    useEffect(() => {
        if (!selectedStation) return;
        apiRequest(`/stations/${selectedStation}/users`, { method: "GET" })
            .then((users) => {
                const officerOptions = users.map((user: any) => ({
                    label: user.fullName,
                    value: user.username
                }));
                setOfficers(officerOptions);
            })
            .catch(console.error);
    }, [selectedStation]);

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
                    onChange: (val: string) => {
                        setSelectedStation(val);
                    },
                    required:true
                },

                {
                    name: "assignedOfficer",
                    label: "Next Officer",
                    type: "select",
                    options: officers, colSpan:6,required:true
                }
            ]
        }
    ];

    return (
        <StepForm
            config={formConfig}
            onSubmit={(data) => {
                console.log("Submitting data:", data);
                apiRequest(`/patient/visits/plan/create`, { method: "POST", body: data })
                    .then((response) => console.log(response))
                    .catch((err) => console.error(err));
            }}
            idFromParent={visitId}
            defaultValues={{
                    patientVisitId: visitId
            }}
            submitButtonLabel={'Assign Patient'}
        />
    );
};

export default PatientAssign;
