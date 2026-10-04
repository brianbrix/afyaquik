import React from 'react';
interface DetailsPageProps {
    fields: {
        label: string;
        accessor: string;
        type?: string;
    }[];
    endpoint: string;
    title?: string;
    otherComponentsToRender?: {
        title: string;
        content: React.ReactNode;
    }[];
    topComponents?: React.ReactNode[];
    activeTab?: string;
}
declare const DetailsPage: React.FC<DetailsPageProps>;
export default DetailsPage;
