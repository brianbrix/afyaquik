"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importStar(require("react"));
const react_hook_form_1 = require("react-hook-form");
const DraftEditor_1 = __importDefault(require("./DraftEditor"));
const react_bootstrap_1 = require("react-bootstrap");
const dateFormatter_1 = require("./dateFormatter");
const StepForm = ({ config = [], onSubmit, defaultValues, idFromParent, submitButtonLabel, bottomComponents, formMethodsRef }) => {
    const methods = (0, react_hook_form_1.useForm)({ defaultValues });
    // const { control, handleSubmit, formState: { errors }, reset, register, setValue, getValues, trigger } = useForm({ defaultValues });
    const { control, handleSubmit, formState: { errors, isSubmitting }, reset, register, setValue, getValues, trigger } = methods;
    const [submitError, setSubmitError] = (0, react_1.useState)('');
    const [step, setStep] = (0, react_1.useState)(0);
    const [formData, setFormData] = (0, react_1.useState)({});
    const [selectedItem, setSelectedItem] = (0, react_1.useState)('');
    const [entryList, setEntryList] = (0, react_1.useState)([]);
    const [wysiwygContent, setWysiwygContent] = (0, react_1.useState)('');
    (0, react_1.useEffect)(() => {
        if (defaultValues) {
            reset(defaultValues);
        }
    }, [defaultValues, reset]);
    (0, react_1.useEffect)(() => {
        if (formMethodsRef) {
            formMethodsRef.current = methods;
        }
    }, [formMethodsRef, methods]);
    const nextStep = () => setStep(step + 1);
    const prevStep = () => setStep(step - 1);
    const isLastStep = step === config.length - 1;
    const currentStep = config[step];
    (0, react_1.useEffect)(() => {
        currentStep.fields.forEach((field) => {
            if (field.type === 'wysiwyg') {
                register(field.name, {
                    required: field.required ? `${field.label} is required` : false,
                });
            }
        });
    }, [currentStep, register]);
    const renderField = (field) => {
        var _a;
        return (react_1.default.createElement("div", { key: field.name, className: "mb-3" },
            react_1.default.createElement("label", { htmlFor: field.name, className: "form-label fw-semibold" },
                field.label,
                field.required && react_1.default.createElement("span", { className: "text-danger" }, " *")),
            react_1.default.createElement(react_hook_form_1.Controller, { name: field.name, control: control, rules: {
                    required: field.required ? `${field.label} is required` : false,
                    min: field.min === undefined ? undefined : { value: field.min, message: `${field.label} must be at least ${field.min}` },
                    max: field.max === undefined ? undefined : { value: field.max, message: `${field.label} must be at most ${field.max}` },
                }, render: ({ field: controllerField }) => {
                    var _a, _b;
                    const isInvalid = (0, react_hook_form_1.get)(errors, field.name);
                    if (field.type === 'select' && field.options) {
                        const handleSelectChange = (e) => {
                            var _a;
                            const value = field.multiple
                                ? Array.from(e.target.selectedOptions, opt => opt.value)
                                : e.target.value;
                            controllerField.onChange(value);
                            (_a = field.onChange) === null || _a === void 0 ? void 0 : _a.call(field, value);
                        };
                        return (react_1.default.createElement("select", Object.assign({ id: field.name, disabled: field.disabled, multiple: field.multiple }, controllerField, { value: controllerField.value || (field.multiple ? [] : ''), onChange: handleSelectChange, className: `form-select ${isInvalid ? 'is-invalid' : ''}` }),
                            !field.multiple && react_1.default.createElement("option", { value: "" }, "Select..."),
                            field.options.map(opt => (react_1.default.createElement("option", { key: opt.value, value: opt.value }, opt.label)))));
                    }
                    else if (field.type === 'wysiwyg') {
                        return react_1.default.createElement(DraftEditor_1.default, { disabled: field.disabled, hidden: field.hidden, value: getValues(field.name) || '', onChange: (value) => {
                                var _a;
                                setValue(field.name, value, { shouldValidate: true });
                                (_a = field.onChange) === null || _a === void 0 ? void 0 : _a.call(field, value);
                            } });
                    }
                    else if (field.type === 'datetime') {
                        // Handle datetime-local input
                        const handleDateTimeChange = (e) => {
                            var _a;
                            const value = e.target.value;
                            controllerField.onChange(value);
                            (_a = field.onChange) === null || _a === void 0 ? void 0 : _a.call(field, value);
                        };
                        const rawValue = controllerField.value;
                        const value = rawValue
                            ? (0, dateFormatter_1.formatForDatetimeLocal)(new Date(rawValue))
                            : '';
                        return (react_1.default.createElement("input", { id: field.name, hidden: field.hidden, disabled: field.disabled, type: "datetime-local", value: value, onChange: handleDateTimeChange, className: `form-control ${isInvalid ? 'is-invalid' : ''}` }));
                    }
                    else if (field.type === 'number' && field.step) {
                        return (react_1.default.createElement("input", Object.assign({ defaultValue: field.defaultValue, hidden: field.hidden, disabled: field.disabled }, controllerField, { id: field.name, value: (_a = controllerField.value) !== null && _a !== void 0 ? _a : '', min: field.min, max: field.max, placeholder: field.placeholder, type: field.type, step: field.step, className: `form-control ${isInvalid ? 'is-invalid' : ''}` })));
                    }
                    else {
                        return (react_1.default.createElement("input", Object.assign({ hidden: field.hidden, defaultValue: field.defaultValue, disabled: field.disabled }, controllerField, { id: field.name, value: (_b = controllerField.value) !== null && _b !== void 0 ? _b : '', min: field.min, max: field.max, placeholder: field.placeholder, type: field.type, className: `form-control ${isInvalid ? 'is-invalid' : ''}` })));
                    }
                } }),
            (0, react_hook_form_1.get)(errors, field.name) && (react_1.default.createElement("div", { className: "invalid-feedback d-block", role: "alert" }, (_a = (0, react_hook_form_1.get)(errors, field.name)) === null || _a === void 0 ? void 0 : _a.message))));
    };
    const submitStep = handleSubmit((data) => __awaiter(void 0, void 0, void 0, function* () {
        var _a;
        setSubmitError('');
        const stepData = Object.assign(Object.assign({}, formData), data);
        const current = config[step];
        (_a = current.multiSelectorWysiwygConfigs) === null || _a === void 0 ? void 0 : _a.forEach(conf => {
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
                const result = yield current.onStepSubmit(stepData, idFromParent);
                setFormData((prev) => (Object.assign(Object.assign({}, prev), result)));
            }
            else {
                setFormData(stepData);
            }
            if (isLastStep) {
                yield onSubmit(stepData);
            }
            else {
                nextStep();
            }
        }
        catch (error) {
            setSubmitError(error instanceof Error ? error.message : 'Unable to save. Please try again.');
        }
    }));
    return (react_1.default.createElement("div", { className: "container-fluid py-5", style: { maxWidth: '1200px' } },
        react_1.default.createElement("div", { className: "row justify-content-center" },
            react_1.default.createElement("div", { className: "col-12" },
                currentStep.topComponents && currentStep.topComponents.length > 0 && (react_1.default.createElement(react_bootstrap_1.Row, { className: "g-3" }, currentStep.topComponents.map((component, idx) => (react_1.default.createElement(react_bootstrap_1.Col, { key: idx, md: 12 }, component))))),
                react_1.default.createElement("form", { onSubmit: submitStep, className: "card shadow-sm p-4 bg-white border-0 rounded-3" },
                    react_1.default.createElement("h4", { className: "mb-4 text-primary text-center fw-semibold" }, currentStep.label),
                    submitError && react_1.default.createElement("div", { className: "alert alert-danger", role: "alert" }, submitError),
                    react_1.default.createElement("div", { className: "row" }, currentStep.fields.map((field, index) => (react_1.default.createElement("div", { key: field.name, className: `col-md-${field.colSpan || 12} ${index > 0 ? 'mt-3 mt-md-0' : ''}` }, renderField(field))))),
                    currentStep.multiSelectorWysiwygConfigs && currentStep.multiSelectorWysiwygConfigs.length > 0 && (react_1.default.createElement(react_bootstrap_1.Row, { className: "mt-4 g-3" }, currentStep.multiSelectorWysiwygConfigs.map((conf, idx) => (react_1.default.createElement(react_bootstrap_1.Col, { key: `wysiwyg-${idx}`, md: 12 },
                        react_1.default.createElement("label", { className: "form-label fw-semibold" }, conf.title),
                        react_1.default.createElement("div", { className: "d-flex align-items-center mb-2" },
                            react_1.default.createElement("select", { className: "form-select me-2", value: selectedItem, onChange: (e) => setSelectedItem(e.target.value) },
                                react_1.default.createElement("option", { value: "" }, conf.selectLabel || 'Select Item'),
                                conf.items.map((item) => (react_1.default.createElement("option", { key: conf.selectedItemName, value: item.value }, item.name))))),
                        react_1.default.createElement(DraftEditor_1.default, { value: wysiwygContent, onChange: setWysiwygContent }),
                        react_1.default.createElement(react_bootstrap_1.Button, { className: "mt-2", onClick: () => {
                                var _a;
                                console.log("Items", conf.items);
                                if (!selectedItem || !wysiwygContent.trim())
                                    return;
                                if (selectedItem) {
                                    const content = wysiwygContent || '';
                                    setEntryList([...entryList, { item: selectedItem, content: content, label: (_a = conf.items.find(x => x.value == selectedItem)) === null || _a === void 0 ? void 0 : _a.name }]);
                                    setSelectedItem('');
                                    setWysiwygContent('');
                                }
                            } }, conf.addButtonLabel || 'Add'),
                        entryList.length > 0 && (react_1.default.createElement("div", { className: "mt-4" },
                            react_1.default.createElement("h6", null, "Items Added:"),
                            react_1.default.createElement("ul", { className: "list-group" }, entryList.map((entry, index) => (react_1.default.createElement("li", { className: "list-group-item", key: index },
                                react_1.default.createElement("strong", null, entry.label),
                                react_1.default.createElement("div", { dangerouslySetInnerHTML: { __html: entry.content } })))))))))))),
                    bottomComponents && bottomComponents.length > 0 && (react_1.default.createElement(react_bootstrap_1.Row, { className: "mt-4 g-3" }, bottomComponents.map((component, idx) => (react_1.default.createElement(react_bootstrap_1.Col, { key: `bottom-${idx}`, md: 12 }, component))))),
                    react_1.default.createElement("div", { className: "d-flex justify-content-between mt-4" },
                        react_1.default.createElement("div", null, step > 0 && (react_1.default.createElement("button", { type: "button", className: "btn btn-outline-secondary me-2", disabled: isSubmitting, onClick: prevStep },
                            react_1.default.createElement("i", { className: "bi bi-arrow-left me-1" }),
                            " Previous"))),
                        react_1.default.createElement("div", null,
                            react_1.default.createElement("button", { type: "submit", disabled: isSubmitting, className: `btn ${isLastStep ? 'btn-success' : 'btn-primary'}` }, isLastStep ? (react_1.default.createElement(react_1.default.Fragment, null,
                                react_1.default.createElement("i", { className: "bi bi-check-circle me-1" }),
                                submitButtonLabel || `Submit ${currentStep.label}`)) : (react_1.default.createElement(react_1.default.Fragment, null,
                                currentStep.stepButtonLabel || 'Continue',
                                react_1.default.createElement("i", { className: "bi bi-arrow-right ms-1" })))))))))));
};
exports.default = StepForm;
