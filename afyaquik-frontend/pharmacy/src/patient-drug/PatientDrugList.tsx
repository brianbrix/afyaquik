import { useEffect, useState } from "react";
import {apiRequest, DataTable, useToast} from "@afyaquik/shared";
import { useParams } from "react-router-dom";
import { Button } from "react-bootstrap";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Drug Name', accessor: 'drugName' },
    { header: 'Quantity', accessor: 'quantity' },
    { header: 'Dosage Instructions', accessor: 'dosageInstructions', type: 'wysiwyg' },
    { header: 'Dispensed', accessor: 'dispensed', type: 'boolean' }
];

interface PatientDrug {
    id: number;
    drugName: string;
    quantity: number;
    dosageInstructions: string;
    dispensed: boolean;
    patientVisitId: number;
}

const searchFields = [
    {
        name: 'drugName',
        label: 'Drug Name',
    },
    {
        name: 'dispensed',
        label: 'Dispensed Status',
    }
];

const PatientDrugList = ({visitId, data: initialData}:{visitId:number, data: PatientDrug[]}) => {
    const [data, setData] = useState<PatientDrug[]>(initialData);
    const [showSelectionMode, setShowSelectionMode] = useState(false);
    const { showToast } = useToast()

    useEffect(() => {
        setData(initialData);
    }, [initialData]);

    const reloadData = () => {
        apiRequest(`/patient-drugs/visit/${visitId}`)
            .then(response => {
                setData(response);
            })
            .catch(error => {
                console.error('Error reloading data:', error);
            });
    };

    const handleDispense = (drug: PatientDrug) => {
        apiRequest(`/patient-drugs/${drug.id}/dispense`, {
            method: 'PUT'
        })
            .then(response => {
                alert('Drug dispensed successfully');
                reloadData();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while dispensing the drug');
            });
    };

    const handleMultipleDispense = (selectedDrugs: PatientDrug[]) => {
        const undispensedDrugs = selectedDrugs.filter(drug => !drug.dispensed);

        if (undispensedDrugs.length === 0) {
            alert('No undispensed drugs selected');
            return;
        }

        const dispensePromises = undispensedDrugs.map(drug =>
            apiRequest(`/patient-drugs/${drug.id}/dispense`, {
                method: 'PUT'
            })
        );

        Promise.all(dispensePromises)
            .then(responses => {
                alert(`${responses.length} drugs dispensed successfully`);
                reloadData();
                setShowSelectionMode(false);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while dispensing the drugs');
                reloadData();
            });
    };

    const toggleSelectionMode = () => {
        setShowSelectionMode(!showSelectionMode);
    };

    return (

        <div>
            <div className="d-flex justify-content-end mb-3">
                <Button
                    variant="primary"
                    onClick={toggleSelectionMode}
                >
                    {showSelectionMode ? 'Cancel' : 'Dispense Multiple Drugs'}
                </Button>
            </div>

            <DataTable
                title="Patient Drugs"
                columns={columns}
                detailsClassName={'primary'}
                editView="index.html#/patient-drugs/#id/edit"
                searchFields={searchFields}
                detailsButtonAction={!showSelectionMode ? handleDispense : undefined}
                detailsTitle={'Dispense Drug'}
                detailsButtonEnabled={(drug) => !drug.dispensed}
                searchEntity="patientDrugs"
                combinedSearchFieldsAndTerms={`patientVisit.id=${visitId}`}
                dataEndpoint={`/search`}
                data={data}
                addTitle="Add Drug"
                addView={`index.html#/patient-drugs/add/${visitId}`}
                showSelectionMode={showSelectionMode}
                selectionModeAction={handleMultipleDispense}
                selectionModeActionTitle="Dispense Selected"
                selectionModeActionDisabled={(selectedItems) =>
                    selectedItems.length === 0 || !selectedItems.some(drug => !drug.dispensed)
                }
            />
        </div>
    );
};

export default PatientDrugList;
