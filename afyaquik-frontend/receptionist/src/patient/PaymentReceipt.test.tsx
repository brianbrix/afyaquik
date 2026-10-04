import React from 'react';
import { render, screen } from '@testing-library/react';
import { ReceiptDocument, Receipt } from './PaymentReceipt';

const receipt: Receipt = {
  receiptNumber: 'AQ-2026-0000000010', billingId: 1, visitId: 2, patientId: 3,
  patientName: 'Synthetic Patient', facilityName: 'Test Facility', paidAt: '2026-10-04T12:00:00+03:00', receivedBy: 'cashier',
  currencyCode: 'KES', amount: 600, paymentMethod: 'MPESA', paymentReference: 'test-reference',
  billTotal: 1000, paidToDate: 600, balance: 400, historicalSnapshot: true, reversed: false, reversalReason: null
};

test('receipt uses server snapshots and does not imply the whole bill is paid', () => {
  render(<ReceiptDocument receipt={receipt} />);
  expect(screen.getByText('AQ-2026-0000000010')).toBeInTheDocument();
  expect(screen.getByText('KES 400.00')).toBeInTheDocument();
  expect(screen.getByText('Balance at payment')).toBeInTheDocument();
  expect(screen.getByText('Test Facility')).toBeInTheDocument();
});

test('reversed and historical records are clearly distinguished', () => {
  render(<ReceiptDocument receipt={{ ...receipt, historicalSnapshot: false, reversed: true, reversalReason: 'Duplicate collection', currencyCode: null }} />);
  expect(screen.getByText('REVERSED')).toBeInTheDocument();
  expect(screen.getByText('Historical Payment Record')).toBeInTheDocument();
  expect(screen.getByText('Original receipt totals and currency were not captured.')).toBeInTheDocument();
});