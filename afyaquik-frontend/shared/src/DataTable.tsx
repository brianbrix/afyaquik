import React, { useState, useEffect } from 'react';
import { Table, Button, Form } from 'react-bootstrap';
import Papa from 'papaparse';
import apiRequest from "./api";
import {FieldConfig} from "./StepConfig";
import {SearchBar} from "./index";

interface DataTableProps<T> {
    title: string;
    columns: { header: string; accessor: string, sortable?: boolean }[];
    data?: T[];
    editView?: string;
    addView?: string;
    detailsView?: string;
    dataEndpoint?: string;
    requestMethod?: string;
    searchFields?: FieldConfig[];
    searchEntity?: string;
    defaultPageSize?: number;
    isSearchable?: boolean;
}

interface PaginatedResponse<T> {
    results: {
        content: T[];
        page: {
            totalElements: number;
            totalPages: number;
            number: number;
            size: number;
        }
    }
}


function DataTable<T extends { id: number }>({
                                                 title,
                                                 columns,
                                                 data: initialData = [],
                                                 editView,
                                                 addView,
                                                 detailsView,
                                                 dataEndpoint,
                                                 requestMethod,
                                                 searchFields = [],
                                                 searchEntity = 'patients',
                                                 defaultPageSize = 10,
                                                 isSearchable
                                             }: DataTableProps<T>) {

    const [searchTerm, setSearchTerm] = useState('');
    const [sortField, setSortField] = useState<string | null>(null);
    const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(defaultPageSize);
    const [data, setData] = useState<T[]>(initialData);
    const [totalElements, setTotalElements] = useState(0);
    const [isSearching, setIsSearching] = useState(false);
    const [selectedFields, setSelectedFields] = useState<FieldConfig[]>(searchFields);
    const [showFieldSelector, setShowFieldSelector] = useState(false);

    const fetchData = async (page: number, size: number, sort?: string) => {
        setIsSearching(true);
        try {
            const params = {
                page,
                size,
                ...(sort && { sort })
            };

            let response;
            console.log("Request Method", requestMethod);
            if (requestMethod === 'GET') {
                const queryParams = new URLSearchParams();
                Object.entries(params).forEach(([key, value]) => {
                    queryParams.append(key, String(value));
                });

                if (searchTerm) {
                    queryParams.append('query', searchTerm);
                    selectedFields.forEach(field => queryParams.append('fields', field.name));
                }

                response = await apiRequest(`${dataEndpoint}?${queryParams.toString()}`);
            } else {
                const requestBody = {
                    ...params,
                    searchEntity: searchEntity,
                    ...(searchTerm && {
                        query: searchTerm,
                        searchFields: selectedFields.map(f=>f.name)
                    })
                };

                response = await apiRequest(dataEndpoint+'', {
                    method: 'POST',
                    body: requestBody,
                });
            }

            console.log("Response", response);
            const result: PaginatedResponse<T> =  response;
            setData(result.results.content);
            setTotalElements(result.results.page.totalElements);
            setCurrentPage(result.results.page.number);
            setPageSize(result.results.page.size);

        } catch (error) {
            console.error('Fetch error:', error);
        } finally {
            setIsSearching(false);
        }
    };

    useEffect(() => {
        const sortParam = sortField ? `${sortField},${sortDirection}` : undefined;
        fetchData(currentPage, pageSize, sortParam);
    }, [currentPage, pageSize, sortField, sortDirection, searchTerm, selectedFields]);

    const handleSort = (field: string) => {
        if (sortField === field) {
            setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
        } else {
            setSortField(field);
            setSortDirection('asc');
        }
        setCurrentPage(0); // Reset to first page when sorting changes
    };

    const handleFieldToggle = (field: FieldConfig) => {
        const newFields = selectedFields.includes(field)
            ? selectedFields.filter(f => f !== field)
            : [...selectedFields, field];
        console.log("Fields",newFields)
        setSelectedFields(newFields);
        setCurrentPage(0); // Reset to first page when search fields change
    };

    const toggleSelectAllFields = () => {
        setSelectedFields(selectedFields.length === searchFields.length ? [] : [...searchFields]);
        setCurrentPage(0);
    };
    function resolveValue(obj: any, path: string, fallback: string = 'N/A'): any {
        return path.split('.').reduce((acc, part) => (acc && acc[part] !== undefined) ? acc[part] : fallback, obj);
    }
    const downloadCSV = () => {
        const csvData = (data).map(record => {
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

    return (
        <div className="container my-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="text-primary fw-semibold m-0">{title}</h5>
                <div>
                    <Button variant="success" className="me-2" onClick={downloadCSV}>
                        <i className="bi bi-download me-1"></i> Download CSV
                    </Button>
                    {addView && (
                        <Button variant="primary" onClick={() => window.location.href = addView}>
                            <i className="bi bi-plus-circle me-1"></i> Add Record
                        </Button>
                    )}
                </div>
            </div>

            {/* Search and Field Selection */}
            {isSearchable && (
                <SearchBar
                    searchTerm={searchTerm}
                    onSearchChange={setSearchTerm}
                    searchFields={searchFields}
                    selectedFields={selectedFields}
                    onToggleField={handleFieldToggle}
                    onToggleSelectAll={toggleSelectAllFields}
                    showFieldSelector={showFieldSelector}
                    setShowFieldSelector={setShowFieldSelector}
                    isLoading={isSearching}
                />
            )}



            <Table bordered hover responsive className="table-sm align-middle">
                <thead className="table-light">
                <tr>
                    {columns.map(col => (
                        <th
                            key={col.accessor}
                            className="py-3 ps-4"
                            style={{ cursor: col.sortable !== false ? 'pointer' : 'default' }}
                            onClick={() => col.sortable !== false && handleSort(col.accessor)}
                        >
                            {col.header}
                            {sortField === col.accessor && (
                                sortDirection === 'asc' ? ' 🔼' : ' 🔽'
                            )}
                        </th>
                    ))}
                    {(editView || detailsView) && <th>Actions</th>}
                </tr>
                </thead>
                <tbody>
                {data.length === 0 ? (
                    <tr>
                        <td colSpan={columns.length + ((editView || detailsView) ? 1 : 0)} className="text-center">
                            No records found
                        </td>
                    </tr>
                ) : (
                    data.map(record => (
                        <tr key={record.id}>
                            {columns.map(col => (
                                <td key={`${record.id}-${col.accessor}`}>
                                    {resolveValue(record, col.accessor)}
                                </td>
                            ))}
                            {(editView || detailsView) && (
                                <td>
                                    {detailsView && (
                                        <Button
                                            variant="secondary"
                                            className="me-2"
                                            onClick={() => window.location.href = detailsView.replace("#id", String(record.id))}
                                        >
                                            <i className="bi bi-eye"></i> Details
                                        </Button>
                                    )}
                                    {editView && (
                                        <Button
                                            variant="primary"
                                            onClick={() => window.location.href = editView.replace("#id", String(record.id))}
                                        >
                                            <i className="bi bi-pencil"></i> Edit
                                        </Button>
                                    )}
                                </td>
                            )}
                        </tr>
                    ))
                )}
                </tbody>
            </Table>

            <div className="d-flex justify-content-between align-items-center mt-3">
                <div>
                    <small className="text-muted">
                        Showing {data.length} of {totalElements} records
                    </small>
                </div>

                <div className="d-flex align-items-center">
                    <select
                        className="form-select form-select-sm me-2"
                        style={{ width: 'auto' }}
                        value={pageSize}
                        onChange={(e) => {
                            setPageSize(Number(e.target.value));
                            setCurrentPage(0);
                        }}
                    >
                        {[5, 10, 20, 50].map(size => (
                            <option key={size} value={size}>{size} per page</option>
                        ))}
                    </select>

                    <nav>
                        <ul className="pagination pagination-sm mb-0">
                            <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
                                <button
                                    className="page-link"
                                    onClick={() => setCurrentPage(p => p - 1)}
                                    disabled={currentPage === 0}
                                >
                                    Prev
                                </button>
                            </li>
                            {Array.from({ length: Math.min(5, Math.ceil(totalElements / pageSize)) }, (_, i) => {
                                // Show pages around current page
                                let pageNum = i;
                                if (currentPage >= 3 && currentPage < Math.ceil(totalElements / pageSize) - 3) {
                                    pageNum = currentPage - 2 + i;
                                } else if (currentPage >= Math.ceil(totalElements / pageSize) - 3) {
                                    pageNum = Math.max(0, Math.ceil(totalElements / pageSize) - 5) + i;
                                }
                                return (
                                    <li
                                        key={pageNum}
                                        className={`page-item ${pageNum === currentPage ? 'active' : ''}`}
                                    >
                                        <button
                                            className="page-link"
                                            onClick={() => setCurrentPage(pageNum)}
                                            disabled={pageNum >= Math.ceil(totalElements / pageSize)}
                                        >
                                            {pageNum + 1}
                                        </button>
                                    </li>
                                );
                            })}
                            <li className={`page-item ${currentPage >= Math.ceil(totalElements / pageSize) - 1 ? 'disabled' : ''}`}>
                                <button
                                    className="page-link"
                                    onClick={() => setCurrentPage(p => p + 1)}
                                    disabled={currentPage >= Math.ceil(totalElements / pageSize) - 1}
                                >
                                    Next
                                </button>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    );
}

export default DataTable;
