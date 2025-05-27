import React, { useEffect, useState } from 'react';
import { Dropdown, Badge } from 'react-bootstrap';
import formatDate from "../dateFormatter";
import {fetchNotifications, markAllAsRead, markAsRead} from "./NotificationService";

const NotificationsBell = ({ userId, userRole }: { userId: number,userRole:string }) => {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        fetchNotifications(setNotifications,userId, userRole);
    }, []);

    return (
        <Dropdown>
            <Dropdown.Toggle variant="light" id="notifications-dropdown">
                <i className="bi bi-bell-fill"></i>
                {notifications.length > 0 && (
                    <Badge bg="danger" className="ms-1">{notifications.length}</Badge>
                )}
            </Dropdown.Toggle>

            <Dropdown.Menu style={{ minWidth: 320 }}>
                <Dropdown.Header className="fw-semibold text-primary">Notifications</Dropdown.Header>
                {notifications.length === 0 ? (
                    <Dropdown.Item disabled>No new notifications</Dropdown.Item>
                ) : (
                    notifications.filter((n: any) => (
                        n.recipientRole === userRole
                    )).map((n: any) => (
                        <Dropdown.Item
                            key={n.id}
                            onClick={() => {
                                markAsRead(n.id, setNotifications, userId, userRole)
                                if (n.targetUrl) window.location.href = n.targetUrl;
                            }}
                            className="text-wrap"
                        >
                            <strong>{n.title}</strong>
                            <div className="small text-muted">{n.message}</div>
                            <div className="small text-secondary">Sent at: {formatDate(n.createdAt)}</div>
                        </Dropdown.Item>
                    ))
                )}
                {notifications.length > 0 && (
                    <Dropdown.Divider />
                )}
                {notifications.length > 0 && (
                    <Dropdown.Item
                        className="text-center text-primary"
                        onClick={async () => {
                           markAllAsRead(setNotifications, userId, userRole)
                        }}
                    >
                        Mark all as read
                    </Dropdown.Item>
                )}
            </Dropdown.Menu>
        </Dropdown>
    );
};

export default NotificationsBell;
