import React from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { apiRequest } from '@afyaquik/shared';
import Papa from 'papaparse';
import ReportsWorkspace from './ReportsWorkspace';

jest.mock('@afyaquik/shared', () => ({ apiRequest: jest.fn() }));

const summary = {
  from: '2026-10-01', to: '2026-10-04', registeredPatients: 12, visits: 20,
  appointments: 8, prescriptions: 5, visitsByStatus: { STARTED: 20 }, appointmentsByStatus: { PENDING: 8 },
};

beforeEach(() => {
  jest.mocked(apiRequest).mockResolvedValue(summary);
  URL.createObjectURL = jest.fn(() => 'blob:activity-report');
  URL.revokeObjectURL = jest.fn();
});

afterEach(() => {
  jest.useRealTimers();
  jest.restoreAllMocks();
});

test('renders aggregate activity returned by the API', async () => {
  render(<ReportsWorkspace />);
  expect(await screen.findByText('Registered patients')).toBeInTheDocument();
  expect(screen.getByText('12')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Export CSV/ })).toBeEnabled();
});

test('exports formula-safe aggregate CSV using an attached link before releasing the blob', async () => {
  const unparse = jest.spyOn(Papa, 'unparse');
  const click = jest.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (this: HTMLAnchorElement) {
    expect(this.isConnected).toBe(true);
    expect(this.download).toBe('afyaquik-activity-2026-10-01-2026-10-04.csv');
  });
  render(<ReportsWorkspace />);
  await screen.findByText('Registered patients');
  jest.useFakeTimers();
  fireEvent.click(screen.getByRole('button', { name: /Export CSV/ }));
  expect(unparse).toHaveBeenCalledWith(expect.arrayContaining([
    { from: summary.from, to: summary.to, metric: 'Registered patients', count: 12 },
    { from: summary.from, to: summary.to, metric: 'Visits: STARTED', count: 20 },
  ]), { escapeFormulae: true });
  expect(click).toHaveBeenCalledTimes(1);
  expect(URL.revokeObjectURL).not.toHaveBeenCalled();
  jest.runOnlyPendingTimers();
  expect(URL.revokeObjectURL).toHaveBeenCalledWith('blob:activity-report');
  expect(document.querySelector('a[download]')).not.toBeInTheDocument();
});
