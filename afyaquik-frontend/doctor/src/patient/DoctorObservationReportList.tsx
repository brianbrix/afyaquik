import React, {useEffect, useState} from "react";
import {apiRequest, ObservationReportList} from "@afyaquik/shared";

const DoctorObservationReportList = function ({visitId}:{visitId:number}){
    const [isLoading, setIsLoading] = useState(true);
    const [reportData, setReportDate]= useState([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchReports = async () => {
            try {
                apiRequest(`/observations/visit/${visitId}?page=0&size=100&sort=createdAt,desc`).then(
                    (response)=>{
                        setReportDate(response.results.content)
                        console.log('Response:', response);
                    }
                )
            } catch (err) {
                setError("Failed to fetch doctor information");
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchReports();
    }, [visitId]);

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    return (
        <div>
            <ObservationReportList reports={reportData} />
        </div>
    );
}
export default DoctorObservationReportList;
