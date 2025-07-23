import {useEffect, useRef, useState} from "react";
import {apiRequest, DataTable, DataTableRef, useAlert, useToast} from "@afyaquik/shared";

const columns = [

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
    const { showAlert } = useAlert();
    const dataTableRef = useRef<DataTableRef<PatientDrug>>(null);

    useEffect(() => {
        setData(initialData);
    }, [initialData]);

    const reloadData = () => {
        if (dataTableRef.current) {
            dataTableRef.current.refreshData();
        }
    };

    const handleDispense = (drug: PatientDrug) => {
        apiRequest(`/patient-drugs/${drug.id}/dispense`, {
            method: 'PUT'
        })
            .then(response => {
                showAlert('Drug dispensed successfully','Drug Dispense', 'success');
                reloadData();
            })
            .catch(error => {
                console.error('Error:', error);
                showAlert(error.message, 'Drug Dispense Error', 'error');
            });
    };
    const handleDrugRemove = (drug: PatientDrug)=>{
        apiRequest(`/patient-drugs/${drug.id}`, {
            method: 'DELETE'
        })
            .then(response => {
                showAlert('Drug removed successfully', 'Drug Remove', 'success');
                reloadData();
            })
            .catch(error => {
                console.error('Error:', error);
                showAlert(error.message, 'Drug Remove Error', 'error');
            });
    }

    const handleMultipleDispense = (selectedDrugs: PatientDrug[]) => {
        const undispensedDrugs = selectedDrugs.filter(drug => !drug.dispensed);

        if (undispensedDrugs.length === 0) {
            showAlert('No undispensed drugs selected', 'Drug Dispense','warning');
            return;
        }

        const dispensePromises = undispensedDrugs.map(drug =>
            apiRequest(`/patient-drugs/${drug.id}/dispense`, {
                method: 'PUT'
            })
        );

        Promise.all(dispensePromises)
            .then(responses => {
                showAlert(`${responses.length} drugs dispensed successfully`,'Drug Dispense', 'success');
                reloadData();
            })
            .catch(error => {
                console.error('Error:', error);
                showAlert(error.message,'Drug Dispense Error', 'error');
                reloadData();
            });
    };

    const deleteMultipleErrorAction =(selectedRows: PatientDrug[]) : boolean=>
    {
    const dispensedDrugs = selectedRows.find(drug=>drug.dispensed)
        if (dispensedDrugs)
        {
            showAlert("You cannot delete dispensed records.Unselect any dispensed records to continue.",'Drug Dispense', 'warning')
            return true;
        }
        return false;
    }
    return (

        <div>


            <DataTable
                title="Patient Drugs"
                columns={columns}
                detailsClassName={'primary'}
                editView="index.html#/patient-drugs/#id/edit"
                searchFields={searchFields}
                detailsButtonAction={handleDispense}
                deleteButtonAction={handleDrugRemove}
                deleteButtonEnabled={(drug: PatientDrug) => !drug.dispensed}
                detailsTitle={'Dispense Drug'}
                detailsButtonEnabled={(drug: PatientDrug) => !drug.dispensed}
                searchEntity="patientDrugs"
                combinedSearchFieldsAndTerms={`patientVisit.id=${visitId},deleted=false`}
                dataEndpoint={`/search`}
                preventDeleteMultipleAction={deleteMultipleErrorAction}
                data={data}
                addTitle="Add Drug"
                addView={`index.html#/patient-drugs/add/${visitId}`}
                showSelectionMode={true}
                showMultipleDeleteButton={true}
                selectionModeAction={handleMultipleDispense}
                selectionModeActionTitle="Dispense Selected"
                onRef={(ref) => dataTableRef.current = ref}
                selectionModeActionDisabled={(selectedItems) =>
                    selectedItems.length === 0 || !selectedItems.some(drug => !drug.dispensed)
                }
            />
        </div>
    );
};

export default PatientDrugList;
