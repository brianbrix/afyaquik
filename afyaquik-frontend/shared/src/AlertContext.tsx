import React, { createContext, useContext, useState, ReactNode } from 'react';
import { Modal, Button } from 'react-bootstrap';

export type AlertType = 'success' | 'error' | 'warning' | 'info';

interface AlertContextProps {
  showAlert: (message: string, title?: string, type?: AlertType) => void;
}

const AlertContext = createContext<AlertContextProps | undefined>(undefined);

export const AlertProvider = ({ children }: { children: ReactNode }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [title, setTitle] = useState('Alert');
  const [alertType, setAlertType] = useState<AlertType>('info');

  const showAlert = (message: string, title: string = 'Alert', type: AlertType = 'info') => {
    setMessage(message);
    setTitle(title);
    setAlertType(type);
    setIsOpen(true);
  };

  const handleClose = () => {
    setIsOpen(false);
  };

  // Get the appropriate Bootstrap variant based on alert type
  const getVariant = (type: AlertType) => {
    switch (type) {
      case 'success': return 'success';
      case 'error': return 'danger';
      case 'warning': return 'warning';
      case 'info': return 'info';
      default: return 'primary';
    }
  };

  return (
    <AlertContext.Provider value={{ showAlert }}>
      {children}
      <Modal show={isOpen} onHide={handleClose} centered>
        <Modal.Header
          closeButton
          className={`bg-${getVariant(alertType)} text-white`}
        >
          <Modal.Title>{title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>{message}</Modal.Body>
        <Modal.Footer>
          <Button variant={getVariant(alertType)} onClick={handleClose}>
            OK
          </Button>
        </Modal.Footer>
      </Modal>
    </AlertContext.Provider>
  );
};

export const useAlert = () => {
  const context = useContext(AlertContext);
  if (!context) throw new Error('useAlert must be used within an AlertProvider');
  return context;
};
