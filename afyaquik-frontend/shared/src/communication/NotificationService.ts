import apiRequest from "../api";

export type NotificationType = 'APPOINTMENT' | 'SYSTEM' | 'VISIT';
export const sendNotification= async (recipientId:number, title:string, message:string, targetUrl:string, type:NotificationType)=>{
    const requestBody = {
        recipientId: recipientId,
        title: title,
        message: message,
        targetUrl: targetUrl,
        type: type
    }
    console.log('Sending notification', requestBody)
    const response = await apiRequest(`/notifications/send`, { method: 'POST', body: requestBody });
    console.log('Notification sent', response)
    return response;

}

export  const fetchNotifications = async (setNotifications:any, userId:number) => {
    const data = await apiRequest(`/notifications/unread/${userId}`);
    setNotifications(data);
};

export   const markAsRead = async (id: number,setNotifications:any, userId:number) => {
    await apiRequest(`/notifications/mark-read/${id}`, { method: 'POST' });
    fetchNotifications(setNotifications, userId);
};

export const markAllAsRead = async (setNotifications:any, userId:number) => {
    await apiRequest(`/notifications/mark-all-read/${userId}`, { method: 'POST' });
    fetchNotifications(setNotifications, userId);
};
