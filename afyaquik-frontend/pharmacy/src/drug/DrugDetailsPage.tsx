import { useParams } from "react-router-dom";
import { DetailsPage, apiRequest } from "@afyaquik/shared";
import { Button } from "react-bootstrap";
import React, { useState } from "react";

interface Drug {
    id: number;
    name: string;
    category: {
        name: string;
    };
    form: {
        name: string;
    };
    strength: string;
    unit: string;
    availableQuantity: number;
}

const components = function (id: any) {
    return (
        <div className="d-flex justify-content-between">
            <Button
                variant="outline-info"
                onClick={() => window.location.href = `index.html#/drugs`}
            >
                Go to Drugs
            </Button>
            <Button
                variant="outline-primary"
                onClick={() => window.location.href = `index.html#/drugs/${id}/edit`}
            >
                Edit
            </Button>
        </div>
    )
}

const DrugDetailsPage = () => {
    let params = useParams();
    const id = Number(params.id);
    const endpoint = `/drugs/${id}`;
    const fields = [
        { label: "ID", accessor: "id" },
        { label: "Name", accessor: "name" },
        { label: "Category", accessor: "category.name" },
        { label: "Form", accessor: "form.name" },
        { label: "Strength", accessor: "strength" },
        { label: "Unit", accessor: "unit" },
        { label: "Available Quantity", accessor: "availableQuantity" }
    ];

    return (
        <div className="container my-4">
            <DetailsPage
                topComponents={[components(id)]}
                title={"Drug Details"}
                endpoint={endpoint}
                fields={fields}
            />
        </div>
    )
}

export default DrugDetailsPage;
