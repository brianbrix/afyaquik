import { DataTable } from "@afyaquik/shared";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Name', accessor: 'name' },
    { header: 'Category', accessor: 'categoryName' },
    { header: 'Form', accessor: 'drugFormName' },
    { header: 'Strength', accessor: 'strength' },
    { header: 'Available Quantity', accessor: 'stockQuantity' }
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
            dataEndpoint={'/search'}
        />
    );
};

export default DrugList;
