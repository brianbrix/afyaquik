import React, { useEffect, useState } from "react";
import { apiRequest, StepForm, StepConfig, useToast } from "@afyaquik/shared";
import { Button } from "react-bootstrap";

const ProfilePage = () => {
  const [profile, setProfile] = useState<any>({});
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const { showToast } = useToast();

  useEffect(() => {
    apiRequest("/users/me", { method: "GET" })
      .then((data) => setProfile(data))
      .finally(() => setLoading(false));
  }, []);

  const formConfig: StepConfig[] = [
    {
      label: "Edit Profile",
      fields: [
        { name: "username", label: "Username", type: "text", disabled: true },
        { name: "firstName", label: "First Name", type: "text", required: true },
        { name: "lastName", label: "Last Name", type: "text", required: true },
        { name: "email", label: "Email", type: "email", required: true },
        { name: "phone", label: "Phone", type: "text" },
      ],
    },
  ];

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      {!editing ? (
        <div>
          <h2>Profile</h2>
          <p><b>Username:</b> {profile.username}</p>
          <p><b>First Name:</b> {profile.firstName}</p>
          <p><b>Last Name:</b> {profile.lastName}</p>
          <p><b>Email:</b> {profile.email}</p>
          <p><b>Phone:</b> {profile.phone}</p>
          <Button variant="primary" onClick={() => setEditing(true)}>
            Edit Profile
          </Button>
        </div>
      ) : (
        <StepForm
          config={formConfig}
          defaultValues={profile}
          onSubmit={(data) => {
            apiRequest(`/users/${profile.id}`, { method: "PUT", body: data })
              .then((updated) => {
                setProfile(updated);
                setEditing(false);
                showToast("Profile updated successfully.", "success");
              })
              .catch(() => showToast("Failed to update profile.", "error"));
          }}
          submitButtonLabel="Save Changes"
        />
      )}
    </div>
  );
};

export default ProfilePage;

