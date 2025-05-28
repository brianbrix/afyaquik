import {apiRequest, FieldConfig, StepConfig, StepForm} from "@afyaquik/shared";
import {Button, Form} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";

export const backtoList = function (){
    return (  <Button
        variant="outline-info"
        className="btn btn-success mb-4"
        onClick={() => window.location.href = "index.html#/patients"}
    >
        <i className="bi bi-arrow-left me-1"></i> Back To List
    </Button>)
}
interface Item {
    id: number;
    name: string;
    description: string | null;
    category: number;
    categoryName: string;
    price: number | null;
    station: number;
    stationName: string;
    mandatory: boolean;
}

function groupItemsIntoSteps(items: Item[]): StepConfig[] {
    const grouped = items.reduce((acc, item) => {
        if (!acc[item.categoryName]) {
            acc[item.categoryName] = [];
        }
        acc[item.categoryName].push(item);
        return acc;
    }, {} as Record<string, Item[]>);

    // Extract all category names in order
    const categoryNames = Object.keys(grouped);

    return categoryNames.map((categoryName, index) => {
        const fields: FieldConfig[] = grouped[categoryName].map((item) => ({
            name: `item_${item.id}`,
            label: item.name,
            type: 'wysiwyg',
            required: item.mandatory,
            colSpan: 4,
        }));

        // Determine the next category name (if it exists)
        const nextCategoryName = categoryNames[index + 1];
        const stepButtonLabel = nextCategoryName
            ? `Save and go to ${nextCategoryName}`
            : 'Submit Report';

        return {
            label: categoryName,
            fields,
            stepButtonLabel,
            topComponents: [backtoList()],
        };
    });
}
const PatientVisitPlanReportsPage = () => {
    const [formConfig, setFormConfig] = useState<StepConfig[]>([]);
    const [loading, setLoading] = useState(true);
    const params = useParams();
    const planId = params.id;
    useEffect(() => {
        if (!planId) {
            setLoading(false);
            return;
        }
        apiRequest(`/patient/visits/plans/${planId}`, { method:'GET' })
            .then((response)=>{
                console.log('Response:', response);
                apiRequest(`/search`, { method:'POST'
                ,
                    body:{
                    searchEntity:'observationItems',
                        query:`station.name=${response.nextStation}`,
                        page: 0, size: 100,
                    }
                })
                    .then((response)=>{
                        console.log('Response:', response);
                        const config = groupItemsIntoSteps(response.results.content);

                        setFormConfig(config)
                    })
                    .catch(error=>{
                        console.error('Error:', error);
                    })
            })
            .catch(error=>{
                console.error('Error:', error);
            })
            .finally(() => {
            setLoading(false);
        });
    }, [planId]);

    if (loading) {
        return <div>Loading form data...</div>;
    }
    if (!formConfig.length) {
        return <div>No form data available.</div>;
    }
    return (
        <StepForm
            config={formConfig}
            onSubmit={(data) => {
                console.log('Form submitted:', data);
            }}
            defaultValues={{}}
            submitButtonLabel="Submit Report"
        />
    );
}
export default PatientVisitPlanReportsPage;
