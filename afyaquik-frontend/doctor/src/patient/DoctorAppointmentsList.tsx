import {apiRequest, AppointmentList} from "@afyaquik/shared";
import React, {useEffect, useState} from "react";

const DoctorAppointmentList = () => {
    const [doctorId, setDoctorId] = useState(0);
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchDoctorId = async () => {
            try {
                const response = await apiRequest(`/users/me`);
                setDoctorId(response.id);
            } catch (err) {
                setError("Failed to fetch doctor information");
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchDoctorId();
    }, []);
    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    return (
        <AppointmentList query={`doctor.id=${doctorId}`}/>
    )
}
export default DoctorAppointmentList;
