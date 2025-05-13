import React, {useEffect, useState} from 'react';
import { useForm, Controller } from 'react-hook-form';
import {FieldConfig, StepConfig} from "./StepConfig";
import DraftEditor from "./DraftEditor";
import {Button} from "react-bootstrap";
import {useParams} from "react-router-dom";

interface StepFormProps {
    config: StepConfig[];
    onSubmit: (data: any) => void;
    defaultValues?: any;
    idFromParent?: number;
}

const StepForm: React.FC<StepFormProps> = ({ config, onSubmit, defaultValues, idFromParent }) => {
    const { control, handleSubmit, formState: { errors }, reset } = useForm({ defaultValues });
    const [step, setStep] = useState(0);
    const [formData, setFormData] = useState<any>({});
    useEffect(() => {
        if (defaultValues) {
            reset(defaultValues);
        }
    }, [defaultValues, reset]);
    const nextStep = () => setStep(step + 1);
    const prevStep = () => setStep(step - 1);

    const isLastStep = step === config.length - 1;
    const currentStep = config[step];

    const renderField = (field: FieldConfig) => {
        return (
            <div key={field.name} className="mb-3">
                <label className="form-label fw-semibold">
                    {field.label}{field.required && <span className="text-danger"> *</span>}
                </label>

                <Controller
                    name={field.name}
                    control={control}
                    rules={{ required: field.required ? `${field.label} is required` : false }}
                    render={({ field: controllerField }) => {
                        const isInvalid = errors[field.name];
                        if (field.type === 'select' && field.options) {
                            return (
                                <select disabled={field.disabled}
                                        multiple={field.multiple}
                                        {...controllerField}
                                        className={`form-select ${isInvalid ? 'is-invalid' : ''}`}
                                >
                                    {!field.multiple && <option value="">Select...</option>}
                                    {field.options.map(opt => (
                                        <option key={opt.value} value={opt.value}>
                                            {opt.label}
                                        </option>
                                    ))}
                                </select>
                            );
                        }
                            else if (field.type === 'wysiwyg')
                        {
                            return <DraftEditor disabled={field.disabled} hidden={field.hidden} value={controllerField.value || ''} onChange={controllerField.onChange} />
                        } else {
                            return (
                                <input hidden={field.hidden} disabled={field.disabled}
                                    {...controllerField}
                                    type={field.type}
                                    className={`form-control ${isInvalid ? 'is-invalid' : ''}`}
                                />
                            );
                        }
                    }}
                />

                {errors[field.name] && (
                    <div className="invalid-feedback">{errors[field.name]?.message as string}</div>
                )}
            </div>
        );
    };

    const submitStep = handleSubmit(async (data) => {
        const stepData = { ...formData, ...data };
        const current = config[step];
        console.log('id', idFromParent)
        console.log('Type of', typeof idFromParent)


        try {
            // If this step has an external save action (like saveVisit())
            if (current.onStepSubmit) {
                const result = await current.onStepSubmit(stepData, idFromParent);
                setFormData((prev: any) => ({ ...prev, ...result }));
            } else {
                setFormData(stepData);
            }

            if (isLastStep) {
                onSubmit(stepData);
            } else {
                nextStep();
            }
        } catch (error) {
            console.error("Step submission failed", error);
        }
    });

    return (
        <div className="container py-5">


            <div className="row justify-content-center">
                {currentStep.listUrl && (
                    <Button variant="outline-info" className="btn btn-success" onClick={() => window.location.href = `${currentStep.listUrl}`}>
                        <i className="bi bi-arrow-left me-1"></i> Back To List
                    </Button>
                )}
                <div className="col-12 col-md-8 col-lg-6">
                    <form onSubmit={submitStep} className="card shadow-sm p-4 bg-white border-0 rounded-3">
                        <h4 className="mb-4 text-primary text-center fw-semibold">{currentStep.label}</h4>
                        {currentStep.fields.map(renderField)}
                        <div className="d-flex justify-content-between mt-4">
                            {step > 0 && (
                                <button type="button" className="btn btn-outline-secondary" onClick={prevStep}>
                                    <i className="bi bi-arrow-left me-1"></i> Back
                                </button>
                            )}
                            <button type="submit" className="btn btn-primary ms-auto">
                                {isLastStep ? (
                                    <>
                                        <i className="bi bi-check-circle me-1"></i> Submit
                                    </>
                                ) : (
                                    <>
                                        Next <i className="bi bi-arrow-right ms-1"></i>
                                    </>
                                )}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default StepForm;
