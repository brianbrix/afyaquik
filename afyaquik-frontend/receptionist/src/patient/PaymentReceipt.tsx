import React, { useEffect, useState } from 'react';
import { apiRequest } from '@afyaquik/shared';
import { Alert, Button, Modal, Spinner } from 'react-bootstrap';
import './PaymentReceipt.css';

export interface Receipt {
  receiptNumber: string;
  billingId: number;
  visitId: number;
  patientId: number;
  patientName: string;
  facilityName: string;
  paidAt: string;
  receivedBy: string | null;
  currencyCode: string | null;
  amount: number;
  paymentMethod: string;
  paymentReference: string;
  billTotal: number | null;
  paidToDate: number | null;
  balance: number | null;
  historicalSnapshot: boolean;
  reversed: boolean;
  reversalReason: string | null;
}

export function ReceiptDocument({ receipt }: { receipt: Receipt }) {
  const money = (value: number | null) => value === null ? 'Not recorded' :
    `${receipt.currencyCode || 'Currency not recorded'} ${value.toLocaleString('en-KE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  return <article className="payment-receipt">
    <header><h2>{receipt.facilityName}</h2><h3>{receipt.historicalSnapshot ? 'Payment Receipt' : 'Historical Payment Record'}</h3><strong>{receipt.receiptNumber}</strong></header>
    {receipt.reversed && <Alert variant="danger"><strong>REVERSED</strong>: {receipt.reversalReason}</Alert>}
    {!receipt.historicalSnapshot && <Alert variant="warning">Original receipt totals and currency were not captured.</Alert>}
    <dl>
      <dt>Patient</dt><dd>{receipt.patientName}</dd>
      <dt>Patient number</dt><dd>{receipt.patientId}</dd>
      <dt>Visit / invoice</dt><dd>{receipt.visitId} / {receipt.billingId}</dd>
      <dt>Payment date (Nairobi)</dt><dd>{new Date(receipt.paidAt).toLocaleString('en-KE', { timeZone: 'Africa/Nairobi' })}</dd>
      <dt>Method</dt><dd>{receipt.paymentMethod}</dd>
      <dt>Reference</dt><dd>{receipt.paymentReference}</dd>
      <dt>Received by</dt><dd>{receipt.receivedBy || 'Not recorded'}</dd>
    </dl>
    <table className="table"><tbody>
      <tr><th>Amount received</th><td>{money(receipt.amount)}</td></tr>
      <tr><th>Invoice total at payment</th><td>{money(receipt.billTotal)}</td></tr>
      <tr><th>Paid to date at payment</th><td>{money(receipt.paidToDate)}</td></tr>
      <tr><th>Balance at payment</th><td>{money(receipt.balance)}</td></tr>
    </tbody></table>
  </article>;
}

export default function PaymentReceipt({ paymentId, onClose }: { paymentId: number; onClose: () => void }) {
  const [receipt, setReceipt] = useState<Receipt | null>(null);
  const [error, setError] = useState('');
  useEffect(() => {
    let active = true;
    setReceipt(null);
    setError('');
    apiRequest(`/billing/payments/${paymentId}/receipt`).then(value => { if (active) setReceipt(value); })
      .catch(failure => { if (active) setError(failure.message || 'Receipt could not be loaded.'); });
    return () => { active = false; };
  }, [paymentId]);
  return <Modal show onHide={onClose} size="lg" className="receipt-modal">
    <Modal.Header closeButton><Modal.Title>Payment Record</Modal.Title></Modal.Header>
    <Modal.Body>
      {error && <Alert variant="danger">{error}</Alert>}
      {!receipt && !error && <Spinner animation="border" role="status"><span className="visually-hidden">Loading receipt</span></Spinner>}
      {receipt && <ReceiptDocument receipt={receipt} />}
    </Modal.Body>
    <Modal.Footer><Button variant="secondary" onClick={onClose}>Close</Button>
      <Button disabled={!receipt} onClick={() => window.print()} title="Print receipt or save as PDF"><i className="bi bi-printer me-2" aria-hidden="true" />Print Receipt</Button>
    </Modal.Footer>
  </Modal>;
}