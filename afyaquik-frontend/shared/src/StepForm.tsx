import React, {useEffect, useState} from 'react';
import { useForm, Controller } from 'react-hook-form';
import {FieldConfig, multiSelectorWysiwygConfig, StepConfig} from "./StepConfig";
import DraftEditor from "./DraftEditor";
import {Button, Col, Row} from "react-bootstrap";
import {useParams} from "react-router-dom";
import {formatForDatetimeLocal} from "./dateFormatter";

interface StepFormProps {
    config: StepConfig[];
    onSubmit: (data: any) => void;
    defaultValues?: any;
    idFromParent?: number;
    submitButtonLabel?: string;
    bottomComponents?: React.ReactNode[];
}

const StepForm: React.FC<StepFormProps> = ({ config=[], onSubmit, defaultValues, idFromParent, submitButtonLabel, bottomComponents }) => {
    const { control, handleSubmit, formState: { errors }, reset, register, setValue, getValues, trigger } = useForm({ defaultValues });
    const [step, setStep] = useState(0);
    const [formData, setFormData] = useState<any>({});
    const [selectedItem, setSelectedItem] = useState<string>('');
    const [entryList, setEntryList] = useState<{ item: string; content: string, label?:string }[]>([]);
    const [wysiwygContent, setWysiwygContent] = useState<string>('');
    useEffect(() => {
        if (defaultValues) {
            reset(defaultValues);
        }
    }, [defaultValues, reset]);


    const nextStep = () => setStep(step + 1);
    const prevStep = () => setStep(step - 1);

    const isLastStep = step === config.length - 1;
    const currentStep = config[step];

    useEffect(() => {
        currentStep.fields.forEach((field) => {
            if (field.type === 'wysiwyg') {
                register(field.name, {
                    required: field.required ? `${field.label} is required` : false,
                });
            }
        });
    }, [currentStep, register]);
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
                            const handleSelectChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
                                const value = field.multiple
                                    ? Array.from(e.target.selectedOptions, opt => opt.value)
                                    : e.target.value;

                                controllerField.onChange(value);
                                field.onChange?.(value);
                            };

                            return (
                                <select
                                    disabled={field.disabled}
                                    multiple={field.multiple}
                                    {...controllerField}
                                    value={controllerField.value || (field.multiple ? [] : '')}
                                    onChange={handleSelectChange}
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
                            return <DraftEditor
                                disabled={field.disabled}
                                hidden={field.hidden}
                                value={getValues(field.name) || ''}
                                onChange={(value) => {
                                    setValue(field.name, value, { shouldValidate: true });
                                    field.onChange?.(value);
                                }}
                            />
                        }
                        else if (field.type === 'datetime') {
                            // Handle datetime-local input
                            const handleDateTimeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
                                const value = e.target.value;
                                controllerField.onChange(value);
                                field.onChange?.(value);
                            };

                            const rawValue = controllerField.value;
                            const value = rawValue
                                ? formatForDatetimeLocal(new Date(rawValue))
                                : '';

                            return (
                                <input
                                    hidden={field.hidden}
                                    disabled={field.disabled}
                                    type="datetime-local"
                                    value={value}
                                    onChange={handleDateTimeChange}
                                    className={`form-control ${isInvalid ? 'is-invalid' : ''}`}
                                />
                            );
                        }
                        else if (field.type==='number' && field.step)
                            {
                                return (
                                    <input defaultValue={field.defaultValue} hidden={field.hidden} disabled={field.disabled}
                                           {...controllerField}
                                           type={field.type}
                                           step={field.step}
                                           className={`form-control ${isInvalid ? 'is-invalid' : ''}`}
                                    />
                                );
                            }
                        else {
                            return (
                                <input hidden={field.hidden} defaultValue={field.defaultValue} disabled={field.disabled}
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
        console.log('Parent ID', idFromParent)
        console.log('Type of', typeof idFromParent)

        current.multiSelectorWysiwygConfigs?.forEach(conf => {
            if (conf.configName) {
                const entryListKey = conf.configName;
                const entryListValue = entryList.map(entry => ({
                    [conf.selectedItemName]: entry.item,
                    [conf.inputValueName || 'content']: entry.content
                }));
                stepData[entryListKey] = entryListValue;
            }
        });


        try {
            // If this step has an external save action (like saveVisit())
            if (current.onStepSubmit) {
                const result = await current.onStepSubmit(stepData, idFromParent);
                setFormData((prev: any) => ({ ...prev, ...result }));
            } else {
                setFormData(stepData);
            }

            if (isLastStep) {
                onSubmit(stepData)
            } else {
                nextStep();
            }
        } catch (error) {
            console.error("Step submission failed", error);
        }
    });

    return (
        <div className="container-fluid py-5" style={{ maxWidth: '1200px' }}>
            <div className="row justify-content-center">
                <div className="col-12">

                    {currentStep.topComponents && currentStep.topComponents.length > 0 &&(
                        <Row className="g-3">
                            {currentStep.topComponents.map((component, idx) => (
                                <Col key={idx} md={12}>
                                    {component}
                                </Col>
                            ))}
                        </Row>
                    )}


                    <form onSubmit={submitStep} className="card shadow-sm p-4 bg-white border-0 rounded-3">
                        <h4 className="mb-4 text-primary text-center fw-semibold">{currentStep.label}</h4>
                        <div className="row">
                            {currentStep.fields.map((field, index) => (
                                <div
                                    key={field.name}
                                    className={`col-md-${field.colSpan || 12} ${index > 0 ? 'mt-3 mt-md-0' : ''}`}
                                >
                                    {renderField(field)}
                                </div>
                            ))}
                        </div>
                        {
                            currentStep.multiSelectorWysiwygConfigs && currentStep.multiSelectorWysiwygConfigs.length > 0 && (
                                <Row className="mt-4 g-3">
                                    {currentStep.multiSelectorWysiwygConfigs.map((conf:multiSelectorWysiwygConfig, idx:number) => (
                                        <Col key={`wysiwyg-${idx}`} md={12}>
                                            <label className="form-label fw-semibold">{conf.title}</label>
                                            <div className="d-flex align-items-center mb-2">
                                                <select
                                                    className="form-select me-2"
                                                    value={selectedItem}
                                                    onChange={(e) => setSelectedItem(e.target.value)}
                                                >
                                                    <option value="">{conf.selectLabel|| 'Select Item'}</option>
                                                    {conf.items.map((item:{name:string,value:any}) => (
                                                        <option key={conf.selectedItemName} value={item.value}>
                                                            {item.name}
                                                        </option>
                                                    ))}
                                                </select>

                                            </div>
                                            <DraftEditor
                                                value={wysiwygContent}
                                                onChange={setWysiwygContent}
                                            />
                                            <Button
                                                className="mt-2"
                                                onClick={() => {
                                                    console.log("Items", conf.items)
                                                    if (!selectedItem || !wysiwygContent.trim()) return;
                                                    if (selectedItem) {
                                                        const content = wysiwygContent || '';
                                                        setEntryList([...entryList, { item: selectedItem, content: content, label:conf.items.find(x=>x.value==selectedItem)?.name }]);
                                                        setSelectedItem('');
                                                        setWysiwygContent('');
                                                    }
                                                }}
                                            >
                                                {conf.addButtonLabel || 'Add'}
                                            </Button>
                                            {entryList.length > 0 && (
                                                <div className="mt-4">
                                                    <h6>Items Added:</h6>
                                                    <ul className="list-group">
                                                        {entryList.map((entry, index) => (
                                                            <li className="list-group-item" key={index}>
                                                                <strong>{entry.label}</strong>
                                                                <div dangerouslySetInnerHTML={{ __html: entry.content }} />
                                                            </li>
                                                        ))}
                                                    </ul>
                                                </div>
                                            )}
                                        </Col>
                                    ))}
                                </Row>
                            )
                        }
                        {bottomComponents && bottomComponents.length > 0 && (
                            <Row className="mt-4 g-3">
                                {bottomComponents.map((component, idx) => (
                                    <Col key={`bottom-${idx}`} md={12}>
                                        {component}
                                    </Col>
                                ))}
                            </Row>
                        )}

                        <div className="d-flex justify-content-between mt-4">
                            <div>
                                {step > 0 && (
                                    <button
                                        type="button"
                                        className="btn btn-outline-secondary me-2"
                                        onClick={prevStep}
                                    >
                                        <i className="bi bi-arrow-left me-1"></i> Previous
                                    </button>
                                )}
                            </div>
                            <div>
                                <button
                                    type="submit"
                                    className={`btn ${isLastStep ? 'btn-success' : 'btn-primary'}`}
                                >
                                    {isLastStep ? (
                                        <>
                                            <i className="bi bi-check-circle me-1"></i>{submitButtonLabel || `Submit ${currentStep.label}`}
                                        </>
                                    ) : (
                                        <>
                                            {`${currentStep.stepButtonLabel}` || 'Continue'}<i className="bi bi-arrow-right ms-1"></i>
                                        </>
                                    )}
                                </button>
                            </div>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    );
};

export default StepForm;
