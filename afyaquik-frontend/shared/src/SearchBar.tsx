import React from 'react';
import { Button, Form } from 'react-bootstrap';
import { FieldConfig } from './StepConfig';

interface SearchBarProps {
    searchTerm: string;
    onSearchChange: (term: string) => void;
    searchFields: FieldConfig[];
    selectedFields: FieldConfig[];
    onToggleField: (field: FieldConfig) => void;
    onToggleSelectAll: () => void;
    showFieldSelector: boolean;
    setShowFieldSelector: (value: boolean) => void;
    isLoading: boolean;
    dateFieldValue: string;
    onDateFieldChange: (value: string) => void;
    onResetFilters: () => void;
    setCurrentPage: (page: number) => void;

}

const SearchBar: React.FC<SearchBarProps> = ({
                                                 searchTerm,
                                                 onSearchChange,
                                                 searchFields,
                                                 selectedFields,
                                                 onToggleField,
                                                 onToggleSelectAll,
                                                 showFieldSelector,
                                                 setShowFieldSelector,
                                                 isLoading,
    dateFieldValue, onDateFieldChange, onResetFilters,setCurrentPage
                                             }) => {
    return (
        <>
            <div className="d-flex justify-content-between align-items-center p-3 bg-light rounded mb-3">
                <div className="position-relative w-50">
                    <div className="input-group">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Search..."
                            value={searchTerm}
                            onChange={(e) => onSearchChange(e.target.value)}
                        />
                        {searchFields.length > 0 && (
                            <Button
                                variant="outline-secondary"
                                onClick={() => setShowFieldSelector(!showFieldSelector)}
                            >
                                <i className={`bi bi-${showFieldSelector ? 'chevron-up' : 'chevron-down'}`}></i> Filter by
                            </Button>
                        )}
                    </div>
                    {isLoading && (
                        <div className="position-absolute top-50 end-0 translate-middle-y me-2">
                            <div className="spinner-border spinner-border-sm text-secondary" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </div>
                        </div>
                    )}
                </div>
                <Form.Group controlId="createdAt" className="ms-3">
                    <Form.Label>Filter by date</Form.Label>
                    <Form.Control
                        type="date"
                        value={dateFieldValue}
                        onChange={(e ) => {
                            onDateFieldChange(e.target.value)
                            setCurrentPage(0);
                        }
                        }
                    />
                </Form.Group>

                <Button variant="outline-danger" onClick={onResetFilters}>
                    <i className="bi bi-x-circle me-1"></i> Reset Filters
                </Button>
            </div>

            {showFieldSelector && searchFields.length > 0 && (
                <div className="bg-white p-3 mb-3 border rounded shadow-sm">
                    <div className="mb-2">
                        <Form.Check
                            type="checkbox"
                            id="select-all-fields"
                            label="Select All"
                            checked={selectedFields.length === searchFields.length}
                            onChange={onToggleSelectAll}
                        />
                    </div>
                    <div className="d-flex flex-wrap gap-3">
                        {searchFields.map(field => (
                            <Form.Check
                                key={field.name}
                                type="checkbox"
                                id={`field-${field.name}`}
                                label={field.label}
                                checked={selectedFields.includes(field)}
                                onChange={() => onToggleField(field)}
                            />
                        ))}
                    </div>
                </div>

            )}

        </>
    );
};

export default SearchBar;
