import React, { useState, useEffect } from 'react';
import { Table, Button, Form } from 'react-bootstrap';
import Papa from 'papaparse';
import apiRequest from "./api";
import {FieldConfig, FieldType} from "./StepConfig";
import {DataTableRef, SearchBar} from "./index";
import formatDate from "./dateFormatter";
import {formatCurrency, formatNumber, formatPercentage, formatWysiwyg} from "./formaterUtils";


interface DataTableProps<T> {
    onRef?: (ref: DataTableRef<T>) => void;
    title: string;
    loading?:boolean;
    error?:any;
    columns: { header: string; accessor: string, sortable?: boolean, type?:string }[];
    data?: T[];
    editView?: string;
    editButtonAction?: (rowData: T) => void;
    editTitle?: string;
    editClassName?: string;
    editButtonEnabled?: boolean | ((row: T) => boolean);
    deleteButtonAction?: (rowData: T) => void;
    deleteTitle?: string;
    deleteClassName?: string;
    deleteButtonEnabled?: boolean | ((row: T) => boolean);
    addView?: string;
    addTitle?: string;
    addClassName?: string;
    detailsView?: string;
    detailsButtonAction?: (rowData: T) => void;
    detailsTitle?: string;
    detailsClassName?: string;
    detailsButtonEnabled?: boolean | ((row: T) => boolean);
    dataEndpoint?: string;
    additionalParams?: Record<string, any>;
    requestMethod?: string;
    searchFields?: FieldConfig[];
    searchEntity?: string;
    combinedSearchFieldsAndTerms?:string;
    defaultPageSize?: number;
    isSearchable?: boolean;
    dateFieldName?:string;
    showSelectionMode?: boolean;
    selectionModeAction?: (selectedItems: T[]) => void;
    selectionModeActionTitle?: string;
    selectionModeActionDisabled?: (selectedItems: T[]) => boolean;
    showPagination?:boolean;
    showMultipleDeleteButton?: boolean;
    deleteMultipleButtonTitle?: string;
    deleteEndpoint?: string;
    preventDeleteMultipleAction?:(selectedRows: T[]) => boolean;
    showDeletedRecords?:boolean;

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
                                                 loading=false,
                                                 error,
                                                 title,
        onRef,
                                                 columns,
                                                 data: initialData = [],
                                                 editView,
                                                 editTitle = '',
                                                 editClassName = 'bi bi-pencil',
                                                deleteButtonEnabled=true,
                                                deleteClassName='bi bi-trash',
                                                deleteButtonAction,
                                                deleteTitle='',

                                                 addView,
                                                 addTitle = 'Add Record',
                                                 addClassName = 'bi bi-plus-circle me-1',
                                                 detailsView,
                                                 detailsTitle = 'Details',
                                                 detailsClassName = 'bi bi-eye',
                                                 dataEndpoint,
                                                 requestMethod,
                                                 searchFields = [],
                                                 searchEntity = 'patients',
                                                 defaultPageSize = 10,
                                                 editButtonAction,
                                                 detailsButtonAction,
                                                 additionalParams,
                                                 editButtonEnabled = true,
                                                 detailsButtonEnabled = true,
                                                 showDeletedRecords = false,
                                                 isSearchable,dateFieldName='createdAt', combinedSearchFieldsAndTerms,
                                                 showSelectionMode = false,
                                                 selectionModeAction,
                                                 selectionModeActionTitle = 'Process Selected',
                                                 selectionModeActionDisabled = (selectedItems) => selectedItems.length === 0,
                                                 showPagination=true,
                                                deleteMultipleButtonTitle,
                                                deleteEndpoint,
                                                showMultipleDeleteButton,
                                                preventDeleteMultipleAction
                                             }: DataTableProps<T>) {

    let [searchTerm, setSearchTerm] = useState('');
    const [sortField, setSortField] = useState<string | null>(null);
    const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(defaultPageSize);
    const [data, setData] = useState<T[]>(initialData);
    const [totalElements, setTotalElements] = useState(0);
    const [isSearching, setIsSearching] = useState(false);
    const [selectedFields, setSelectedFields] = useState<FieldConfig[]>(searchFields);
    const [showFieldSelector, setShowFieldSelector] = useState(false);
    const [dateFieldValue, setDateFieldValue] = useState('');
    const [selectedRows, setSelectedRows] = useState<T[]>([]);
    const [selectAll, setSelectAll] = useState(false);


    const onResetFilters = () => {
        setSearchTerm('');
        setDateFieldValue('');
        setSelectedFields([]);
        setCurrentPage(0);
    };

    // Selection mode handlers
    const handleSelectRow = (row: T, isSelected: boolean) => {
        if (isSelected) {
            setSelectedRows([...selectedRows, row]);
        } else {
            setSelectedRows(selectedRows.filter(r => r.id !== row.id));
        }
    };
    useEffect(() => {
        if (onRef) {
            onRef({
                refreshData: () => {
                    fetchData(currentPage, pageSize, sortField ? `${sortField},${sortDirection}` : undefined);
                },
                getSelectedRows: () => selectedRows
            });
        }
    }, [currentPage, pageSize, sortField, sortDirection, selectedRows]);


    const handleDeleteMultiple = async () => {
        if (selectedRows.length === 0) {
            return; // No records selected
        }
        if (typeof preventDeleteMultipleAction === 'function')
        {
            if (preventDeleteMultipleAction(selectedRows)){
                return;
            }
        }
        const confirmDelete = window.confirm(`Are you sure you want to delete ${selectedRows.length} records?`);
        if (!confirmDelete) {
            return;
        }
        try {
            if (!deleteEndpoint) {//use default global delete endpoint
                await apiRequest('/delete', {
                    method: 'POST',
                    body: {
                        ids: selectedRows.map(row => row.id),
                        entityName: searchEntity
                    },
                });
            }

            // Refresh data after deletion
            fetchData(currentPage, pageSize, sortField ? `${sortField},${sortDirection}` : undefined);
            setSelectedRows([]); // Clear selected rows after deletion
        } catch (error) {
            console.error('Delete error:', error);
        }
    };



    const handleSelectAll = (isSelected: boolean) => {
        setSelectAll(isSelected);
        if (isSelected) {
            setSelectedRows([...data]);
        } else {
            setSelectedRows([]);
        }
    };

    // Check if a row is selected
    const isRowSelected = (row: T) => {
        return selectedRows.some(r => r.id === row.id);
    };
    const fetchData = async (page: number, size: number, sort?: string) => {
        setIsSearching(true);
        if (!showDeletedRecords)
        {
            searchTerm+='deleted=false'
        }
        try {
            let params = {
                page,
                size,
                ...(sort && { sort }),
                ...((dateFieldValue && dateFieldName) && { dateFilter : dateFieldName?dateFieldName+'#'+dateFieldValue : 'createdAt'+'#'+dateFieldValue }),
                ...(searchEntity && { searchEntity: searchEntity}),
                ...(additionalParams)
            };

            let response;
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

                if (combinedSearchFieldsAndTerms)
                {
                    if (searchTerm.length > 0)
                    {
                        searchTerm+=',';
                    }
                    searchTerm+=combinedSearchFieldsAndTerms;
                }
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

            const result: PaginatedResponse<T> = response;
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
    if (dataEndpoint) {
        useEffect(() => {
            if (searchTerm.length >= 3 || searchTerm.length === 0) {
                let sortParam = sortField ? `${sortField},${sortDirection}` : 'createdAt,desc';
                if (dateFieldName)
                    sortParam = `${dateFieldName},desc`;
                fetchData(currentPage, pageSize, sortParam);
            }

        }, [currentPage, pageSize, sortField, sortDirection, searchTerm, selectedFields, dateFieldValue]);
    }

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
        setSelectedFields(newFields);
        setCurrentPage(0); // Reset to first page when search fields change
    };

    const toggleSelectAllFields = () => {
        setSelectedFields(selectedFields.length === searchFields.length ? [] : [...searchFields]);
        setCurrentPage(0);
    };
    /**
     * Resolves a value from an object using a dot-notation path
     * @param obj - The object to extract value from
     * @param path - Dot-notation path (e.g., 'user.address.city')
     * @param fallback - Value to return if path doesn't exist
     * @returns The resolved value or fallback
     */
    function resolveValue(obj: any, path: string, fallback: string = 'N/A'): any {
        return path.split('.').reduce((acc, part) => (acc && acc[part] !== undefined) ? acc[part] : fallback, obj);
    }

    /**
     * Formats a cell value based on its type
     * @param value - The raw value to format
     * @param type - The type of formatting to apply
     * @returns The formatted value
     */
    function formatCellValue(value: any, type?: string): any {
        if (value === undefined || value === null) {
            return 'N/A';
        }

        switch (type) {
            case 'date':
                return formatDate(value, true);
            case 'datetime':
                return formatDate(value);
            case 'boolean':
                return value ? 'Yes' : 'No';
            case 'currency':
                return formatCurrency(value);
            case 'number':
                return formatNumber(value);
            case 'percentage':
                return formatPercentage(value);
            case 'wysiwyg':
                return formatWysiwyg(value);
            default:
                return value;
        }
    }
    const downloadCSV = () => {
        const csvData = (data).map(record => {
            const row: any = {};
            columns.forEach(col => {
                row[col.header] = resolveValue(record, col.accessor);
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
    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div className="container my-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="text-primary fw-semibold m-0">{title}</h5>
                <div>
                    {showSelectionMode && selectionModeAction && (
                        <Button
                            variant="success"
                            className="me-2"
                            onClick={() => selectionModeAction(selectedRows)}
                            disabled={selectionModeActionDisabled(selectedRows)}
                        >
                            {selectionModeActionTitle}
                        </Button>
                    )}
                    <Button variant="success" className="me-2" onClick={downloadCSV}>
                        <i className="bi bi-download me-1"></i> Download CSV
                    </Button>
                    {addView && (
                        <Button variant="primary" className="me-2" onClick={() => window.location.href = addView}>
                            <i className={addClassName}></i> {addTitle}
                        </Button>
                    )}
                    {showMultipleDeleteButton && (
                        <Button
                            variant="danger"
                            className="me-2"
                            onClick={handleDeleteMultiple}
                            disabled={selectedRows.length === 0}
                        >
                            {deleteMultipleButtonTitle || 'Delete Selected'}
                        </Button>
                    )}
                </div>
            </div>

            {showSelectionMode && (
                <div className="mb-3">
                    <Form.Check
                        type="checkbox"
                        label="Select All"
                        checked={selectAll}
                        onChange={(e) => handleSelectAll(e.target.checked)}
                    />
                </div>
            )}

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
                    dateFieldValue={dateFieldValue}
                    onDateFieldChange={setDateFieldValue}
                    onResetFilters={onResetFilters}
                    setCurrentPage={setCurrentPage}

                />
            )}



            <Table bordered hover responsive className="table-sm align-middle">
                <thead className="table-light">
                <tr>
                    <th className="py-3 ps-4">#</th>

                    {showSelectionMode && (
                        <th className="py-3 ps-4" style={{ width: '50px' }}>
                            Select
                        </th>
                    )}
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
                    {(editView || detailsView || (!showSelectionMode && (detailsButtonAction || editButtonAction))) && <th>Actions</th>}
                </tr>
                </thead>
                <tbody>
                {data.length === 0 ? (
                    <tr>
                        <td colSpan={columns.length + (showSelectionMode ? 1 : 0) + ((editView || detailsView || (!showSelectionMode && (detailsButtonAction || editButtonAction))) ? 1 : 0)} className="text-center">
                            No records found
                        </td>
                    </tr>
                ) : (
                    data.map((record,index) => (
                        <tr key={record.id}>
                            <td>{(currentPage * pageSize) + index + 1}</td>
                            {showSelectionMode && (
                                <td>
                                    <Form.Check
                                        type="checkbox"
                                        checked={isRowSelected(record)}
                                        onChange={(e) => handleSelectRow(record, e.target.checked)}
                                    />
                                </td>
                            )}
                            {columns.map(col => {
                                const value = resolveValue(record, col.accessor);
                                const display = formatCellValue(value, col.type);
                                return (
                                    <td key={`${record.id}-${col.accessor}`}>
                                        {display}
                                    </td>
                                );
                            })}
                            {(editView || detailsView || (!showSelectionMode && (detailsButtonAction || editButtonAction))) && (
                                <td>
                                    {(detailsView || detailsButtonAction) && (
                                        <Button disabled={ typeof detailsButtonEnabled === 'function'
                                            ? !detailsButtonEnabled(record)
                                            : !detailsButtonEnabled
                                        }
                                            variant="secondary"
                                            className="me-2"
                                            onClick={() => {
                                                if (detailsButtonAction) {
                                                    detailsButtonAction(record);
                                                }
                                                else if (detailsView) {
                                                    window.location.href = detailsView.replace("#id", String(record.id))
                                                }
                                            }}
                                        >
                                            <i className={detailsClassName}></i> {detailsTitle}
                                        </Button>
                                    )}
                                    {(editView || editButtonAction) && (
                                        <Button
                                            disabled={  typeof editButtonEnabled === 'function'
                                                ? !editButtonEnabled(record)
                                                : !editButtonEnabled
                                            }
                                            variant="primary"
                                            onClick={() => {
                                                if (editButtonAction) {
                                                    editButtonAction(record);
                                                }
                                                else if (editView) {
                                                    window.location.href = editView.replace("#id", String(record.id))
                                                }
                                            }}
                                        >
                                            <i className={editClassName}></i> {editTitle}
                                        </Button>
                                    )}
                                    {deleteButtonAction && (
                                        <Button
                                            disabled={ typeof deleteButtonEnabled === 'function'
                                                ? !deleteButtonEnabled(record)
                                                : !deleteButtonEnabled
                                            }
                                            variant="danger"
                                            className="ms-2"
                                            onClick={() => deleteButtonAction(record)}
                                        >
                                            <i className={deleteClassName}></i> {deleteTitle}
                                        </Button>
                                    )}
                                </td>
                            )}
                        </tr>
                    ))
                )}
                </tbody>
            </Table>
            {
                showPagination && (
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
                )
            }


        </div>
    );
}

export default DataTable;
