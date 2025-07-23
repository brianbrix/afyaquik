export default interface DataTableRef<T> {
    refreshData: () => void;
    getSelectedRows: () => T[];
}
