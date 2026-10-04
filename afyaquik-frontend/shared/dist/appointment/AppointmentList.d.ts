import React from "react";
interface AppointmentListProps {
    patientId?: number;
    data?: any;
    title?: string;
    query?: string;
    canEdit?: boolean;
}
declare const AppointmentList: React.FC<AppointmentListProps>;
export default AppointmentList;
