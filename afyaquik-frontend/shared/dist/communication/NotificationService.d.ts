export type NotificationType = 'APPOINTMENT' | 'SYSTEM' | 'VISIT';
export declare const sendNotification: (recipientId: any, title: string, message: string, targetUrl: string, type: NotificationType, recipientRole: string) => Promise<any>;
export declare const fetchNotifications: (setNotifications: any, userId: number, roleName: string) => Promise<void>;
export declare const markAsRead: (id: number, setNotifications: any, userId: number, roleName: string) => Promise<void>;
export declare const markAllAsRead: (setNotifications: any, userId: number, roleName: string) => Promise<void>;
