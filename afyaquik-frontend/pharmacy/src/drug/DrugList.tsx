import { useEffect, useState } from "react";
import { apiRequest, DataTable } from "@afyaquik/shared";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Name', accessor: 'name' },
    { header: 'Category', accessor: 'category.name' },
    { header: 'Form', accessor: 'form.name' },
    { header: 'Strength', accessor: 'strength' },
    { header: 'Unit', accessor: 'unit' },
    { header: 'Available Quantity', accessor: 'availableQuantity' }
];

const searchFields = [
    {
        name: 'name',
        label: 'Drug Name',
    },
    {
        name: 'category.name',
        label: 'Category',
    },
    {
        name: 'form.name',
        label: 'Form',
    },
    {
        name: 'strength',
        label: 'Strength',
    },
    {
        name: 'unit',
        label: 'Unit',
    }
];

const DrugList = () => {
    return (
        <DataTable
            title="Drug Inventory"
            columns={columns}
            detailsView="index.html#/drugs/#id/details"
            searchFields={searchFields}
            searchEntity="drugs"
            isSearchable={true}
            addTitle="Add Drug"
        />
    );
};

export default DrugList;
