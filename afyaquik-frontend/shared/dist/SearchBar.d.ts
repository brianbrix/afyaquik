import React from 'react';
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
declare const SearchBar: React.FC<SearchBarProps>;
export default SearchBar;
