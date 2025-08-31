import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {StepForm, apiRequest, useToast, StepConfig} from "@afyaquik/shared";
import { Button } from "react-bootstrap";

const DoctorAppointmentEditPage = () => {
  const { id } = useParams();
  const appointmentId = Number(id);
  const [defaultValues, setDefaultValues] = useState<any>({});
  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    apiRequest(`/appointments/${appointmentId}`, { method: "GET" })
      .then((data) => {
        setDefaultValues(data);
      })
      .finally(() => setLoading(false));
  }, [appointmentId]);

  const formConfig: StepConfig[] = [
    {
      label: "Edit Appointment",
      fields: [
        {
          name: "notes",
          label: "Appointment Notes",
            type:'wysiwyg',
          required: false,
        },
        {
          name: "status",
          label: "Status",
          type: "select",
          options: [
            { label: "Scheduled", value: "SCHEDULED" },
            { label: "Completed", value: "COMPLETED" },
            { label: "Cancelled", value: "CANCELLED" },
          ],
          required: true,
        },
      ],
    },
  ];

  const handleConvertToVisit = () => {
    apiRequest(`/appointments/${appointmentId}/convert-to-visit`, { method: "POST" })
      .then(() => {
        showToast("Appointment converted to visit and sent to receptionist for reassignment.", "success");
      })
      .catch(() => showToast("Failed to convert appointment.", "error"));
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <StepForm
        config={formConfig}
        defaultValues={defaultValues}
        onSubmit={(data) => {
          // Merge all initial appointment details with new updates
          const updatedData = { ...defaultValues, ...data };
          apiRequest(`/appointments/${appointmentId}`, {
            method: "PUT",
            body: updatedData,
          })
            .then(() => {
              showToast("Appointment updated.", "success");
              navigate(`/appointments/${appointmentId}/details`);
            })
            .catch(() => showToast("Failed to update appointment.", "error"));
        }}
        submitButtonLabel="Save Changes"
      />
      <Button variant="primary" className="mt-3" onClick={handleConvertToVisit}>
        Convert to Visit
      </Button>
    </div>
  );
};

export default DoctorAppointmentEditPage;
