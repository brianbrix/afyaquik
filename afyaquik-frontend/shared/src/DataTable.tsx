import React, { useState } from 'react';
import { Table, Button, Modal } from 'react-bootstrap';
import { useForm, Controller } from 'react-hook-form';
import { FieldConfig } from './StepConfig';
import Papa from 'papaparse';


interface DataTableProps<T> {
    title: string;
    columns: { header: string; accessor: string}[];
    data: T[];
    fields: FieldConfig[];
    onAdd: (record: T) => void;
    onEdit: (record: T) => void;
}

function DataTable<T extends { id: number }>({ title, columns, data, fields, onAdd, onEdit }: DataTableProps<T>) {
    const [showModal, setShowModal] = useState(false);
    const [editingRecord, setEditingRecord] = useState<T | null>(null);
    const { control, handleSubmit, reset } = useForm<T>();

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);

    const totalPages = Math.ceil(data.length / pageSize);

    const paginatedData = data.slice(
        (currentPage - 1) * pageSize,
        currentPage * pageSize
    );

    const openAddModal = () => {
        setEditingRecord(null);
        reset();
        setShowModal(true);
    };

    const openEditModal = (record: T) => {
        setEditingRecord(record);
        reset(record);
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
    };

    const onSubmit = (formData: T) => {
        if (editingRecord) {
            onEdit({ ...editingRecord, ...formData });
        } else {
            onAdd(formData);
        }
        setShowModal(false);
    };

    const downloadCSV = () => {
        const csvData = data.map(record => {
            const row: any = {};
            columns.forEach(col => {
                row[col.header] = record[col.accessor as keyof T];
            });
            return row;
        });

        const csv = Papa.unparse(csvData);

        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `${title.replace(/\s+/g, '_').toLowerCase()}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const renderField = (field: FieldConfig) => {
        return (
            <div key={field.name} className="mb-3">
                <label className="form-label fw-semibold">
                    {field.label}{field.required && <span className="text-danger"> *</span>}
                </label>
                <Controller
                    name={field.name as any}
                    control={control}
                    rules={{ required: field.required ? `${field.label} is required` : false }}
                    render={({ field: controllerField }) => {
                        if (field.type === 'select' && field.options) {
                            return (
                                <select
                                    {...controllerField}
                                    className="form-select"
                                >
                                    <option value="">Select...</option>
                                    {field.options.map(opt => (
                                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                                    ))}
                                </select>
                            );
                        } else {
                            return (
                                <input
                                    {...controllerField}
                                    type={field.type}
                                    className="form-control"
                                />
                            );
                        }
                    }}
                />
            </div>
        );
    };

    return (
        <>
            <div className="container my-4">

            <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="text-primary fw-semibold m-0">{title}</h5>
                <div>
                    <Button variant="success" className="me-2" onClick={downloadCSV}>
                        <i className="bi bi-download me-1"></i> Download CSV
                    </Button>
                    <Button variant="primary" className="btn btn-primary" onClick={() => window.location.href = 'index.html#/add'}>
                        <i className="bi bi-plus-circle me-1"></i> Add Record
                    </Button>

                </div>
            </div>

            <Table bordered hover responsive className="table-sm align-middle">
                <thead className="table-light">
                <tr>
                    {columns.map(col => <th key={col.accessor as string}>{col.header}</th>)}
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {paginatedData.length === 0 ? (
                    <tr><td colSpan={columns.length + 1} className="text-center">No records found</td></tr>
                ) : data.map(record => (
                    <tr key={record.id}>
                        {columns.map(col => <td key={col.accessor as string}>{record[col.accessor as keyof T] as any}</td>)}
                        <td>
                            <Button variant="primary" className="btn btn-primary" onClick={() => window.location.href = `index.html#/edit/${record.id}`}>
                                <i className="bi bi-pencil"></i> Edit
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>

            <div className="d-flex justify-content-between align-items-center mt-3">
                <div>
                    <small className="text-muted">
                        Showing {paginatedData.length} of {data.length} records
                    </small>
                </div>

                <div className="d-flex align-items-center">
                    <select
                        className="form-select form-select-sm me-2"
                        style={{ width: 'auto' }}
                        value={pageSize}
                        onChange={(e) => {
                            setPageSize(Number(e.target.value));
                            setCurrentPage(1);
                        }}
                    >
                        {[5, 10, 20, 50].map(size => (
                            <option key={size} value={size}>{size} per page</option>
                        ))}
                    </select>

                    <nav>
                        <ul className="pagination pagination-sm mb-0">
                            <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
                                <button className="page-link" onClick={() => setCurrentPage(p => p - 1)}>
                                    Prev
                                </button>
                            </li>
                            {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                                <li key={page} className={`page-item ${page === currentPage ? 'active' : ''}`}>
                                    <button className="page-link" onClick={() => setCurrentPage(page)}>
                                        {page}
                                    </button>
                                </li>
                            ))}
                            <li className={`page-item ${currentPage === totalPages ? 'disabled' : ''}`}>
                                <button className="page-link" onClick={() => setCurrentPage(p => p + 1)}>
                                    Next
                                </button>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>

            <Modal show={showModal} onHide={closeModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>{editingRecord ? 'Edit Record' : 'Add Record'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        {fields.map(renderField)}
                        <div className="d-flex justify-content-end">
                            <Button variant="secondary" onClick={closeModal} className="me-2">Cancel</Button>
                            <Button variant="primary" type="submit">{editingRecord ? 'Update' : 'Save'}</Button>
                        </div>
                    </form>
                </Modal.Body>
            </Modal>
            </div>
        </>
    );
}

export default DataTable;
