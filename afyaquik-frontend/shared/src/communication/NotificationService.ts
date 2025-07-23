import apiRequest from "../api";

export type NotificationType = 'APPOINTMENT' | 'SYSTEM' | 'VISIT';
export const sendNotification= async (recipientId:any, title:string, message:string, targetUrl:string, type:NotificationType, recipientRole:string)=>{
    const requestBody = {
        recipientId: recipientId,
        title: title,
        message: message,
        targetUrl: targetUrl,
        type: type,
        recipientRole: recipientRole
    }
    console.log('Sending notification', requestBody)
    const response = await apiRequest(`/notifications/send`, { method: 'POST', body: requestBody });
    console.log('Notification sent', response)
    return response;

}

export  const fetchNotifications = async (setNotifications:any, userId:number, roleName:string) => {
    const data = await apiRequest(`/notifications/unread/${userId}?roleName=${roleName}`);
    setNotifications(data);
};

export   const markAsRead = async (id: number,setNotifications:any, userId:number, roleName:string) => {
    await apiRequest(`/notifications/mark-read/${id}`, { method: 'PUT' });
    fetchNotifications(setNotifications, userId, roleName);
};

export const markAllAsRead = async (setNotifications:any, userId:number, roleName:string) => {
    await apiRequest(`/notifications/mark-all-read/${userId}?roleName=${roleName}`, { method: 'PUT' });
    fetchNotifications(setNotifications, userId,roleName);
};
