import {apiRequest, AssignmentsList} from "@afyaquik/shared";
import {useParams} from "react-router-dom";
import React, {useState} from "react";
import {Dropdown, DropdownButton, Modal} from "react-bootstrap";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Attending Officer', accessor: 'attendingOfficerUserName' },
    { header: 'Assigned Officer', accessor: 'assignedOfficer' },
    { header: 'Next Station', accessor: 'nextStation' },
    { header: 'Status', accessor: 'assignmentStatus' }
];

const ReceptionAssignmentsList = () => {
    let params = useParams();
    const id = Number(params.id);
    const [showStatusModal, setShowStatusModal] = useState(false);
    const [selectedAssignment, setSelectedAssignment] = useState<any>(null);

    const updateStatus = (assignmentId: number, newStatus: string) => {
        apiRequest(`/patient/visits/assignments/status-update/${assignmentId}?status=${newStatus}`, { method: 'PATCH' })
            .then(() => {
                alert(`Assignment status updated to ${newStatus}`);
                setShowStatusModal(false);
                // Force refresh by changing the key on AssignmentsList
                window.location.reload();
            })
            .catch(err => {
                console.error("Error updating status:", err);
                alert("Failed to update status. Please try again.");
            });
    };

    const handleEditClick = (assignment: any) => {
        setSelectedAssignment(assignment);
        setShowStatusModal(true);
    };

    const StatusUpdateModal = () => (
        <Modal show={showStatusModal} onHide={() => setShowStatusModal(false)}>
            <Modal.Header closeButton>
                <Modal.Title>Update Assignment Status</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {selectedAssignment && (
                    <div>
                        <p><strong>Assignment ID:</strong> {selectedAssignment.id}</p>
                        <p><strong>Patient:</strong> {selectedAssignment.patientName}</p>
                        <p><strong>Current Status:</strong> {selectedAssignment.assignmentStatus || 'PENDING'}</p>
                        <DropdownButton id="status-dropdown" title="Select New Status" className="mt-3">
                            <Dropdown.Item onClick={() => updateStatus(selectedAssignment.id, "COMPLETED")}>COMPLETED</Dropdown.Item>
                            <Dropdown.Item onClick={() => updateStatus(selectedAssignment.id, "CANCELLED")}>CANCELLED</Dropdown.Item>
                            <Dropdown.Item onClick={() => updateStatus(selectedAssignment.id, "IN_PROGRESS")}>IN_PROGRESS</Dropdown.Item>
                            <Dropdown.Item onClick={() => updateStatus(selectedAssignment.id, "PENDING")}>PENDING</Dropdown.Item>
                        </DropdownButton>
                    </div>
                )}
            </Modal.Body>
        </Modal>
    );

    return (
        <div>
            <AssignmentsList
                visitId={id}
                columns={columns}
                addView="justAdd"
                editTitle="Update Status"
                editClassName="bi bi-pencil-square"
                editButtonAction={handleEditClick}
            />
            <StatusUpdateModal />
        </div>
    );
}

export default ReceptionAssignmentsList
