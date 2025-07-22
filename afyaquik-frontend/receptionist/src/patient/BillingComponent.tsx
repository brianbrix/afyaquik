import React, { useState, useEffect } from 'react';
import { apiRequest, DataTable, StepForm, StepConfig, FieldConfig, useAlert } from '@afyaquik/shared';
import { Button, Card, Modal } from 'react-bootstrap';

interface BillingProps {
  visitId: number;
}

interface BillingDetail {
  id: number;
  billingId: number;
  billingItemId: number;
  billingItemName: string;
  amount: number;
  description: string;
  quantity: number;
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
}

interface BillingItem {
  id: number;
  name: string;
  description: string;
  defaultAmount: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

interface Payment {
  id: number;
  billingId: number;
  amount: number;
  paymentMethod: string;
  paymentReference: string;
  paymentDate: string;
  notes: string;
  createdAt: string;
  updatedAt: string;
}

interface Billing {
  id: number;
  patientVisitId: number;
  patientName: string;
  amount: number;
  discount: number;
  totalAmount: number;
  description: string;
  status: string;
  paidAt: string;
  paymentMethod: string;
  paymentReference: string;
  amountDue: number;
  billingDetails: BillingDetail[];
  payments: Payment[];
  createdAt: string;
  updatedAt: string;
}

const BillingComponent: React.FC<BillingProps> = ({ visitId }) => {
  const { showAlert } = useAlert();
  const [billing, setBilling] = useState<Billing | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateModal, setShowCreateModal] = useState<boolean>(false);
  const [showPaymentModal, setShowPaymentModal] = useState<boolean>(false);
  const [showAddItemModal, setShowAddItemModal] = useState<boolean>(false);
  const [showEditItemModal, setShowEditItemModal] = useState<boolean>(false);
  const [showEditBillingModal, setShowEditBillingModal] = useState<boolean>(false);
  const [billingItems, setBillingItems] = useState<BillingItem[]>([]);
  const [selectedBillingItem, setSelectedBillingItem] = useState<BillingItem | null>(null);
  const [selectedBillingDetail, setSelectedBillingDetail] = useState<BillingDetail | null>(null);
  const [loadingItems, setLoadingItems] = useState<boolean>(false);

  // Form states
  const [amount, setAmount] = useState<number>(0);
  const [discount, setDiscount] = useState<number>(0);
  const [totalAmount, setTotalAmount] = useState<number>(0);
  const [description, setDescription] = useState<string>('');
  const [paymentMethod, setPaymentMethod] = useState<string>('');
  const [paymentReference, setPaymentReference] = useState<string>('');
  const [paymentAmount, setPaymentAmount] = useState<number>(0);
  const [paymentNotes, setPaymentNotes] = useState<string>('');

  // Billing detail form states
  const [detailAmount, setDetailAmount] = useState<number>(0);
  const [detailDescription, setDetailDescription] = useState<string>('');
  const [detailQuantity, setDetailQuantity] = useState<number>(1);

  useEffect(() => {
    fetchBilling();
    fetchBillingItems();
  }, [visitId]);

  useEffect(() => {
    // Calculate total amount when amount or discount changes
    setTotalAmount(amount - discount);
  }, [amount, discount]);

  useEffect(() => {
    // Set detail amount from selected billing item
    if (selectedBillingItem) {
      setDetailAmount(selectedBillingItem.defaultAmount);
      setDetailDescription(selectedBillingItem.description);
    }
  }, [selectedBillingItem]);

  useEffect(() => {
    // Set payment amount to amount due when billing changes
    if (billing) {
      setPaymentAmount(billing.amountDue);
    }
  }, [billing]);



  const fetchBilling = async () => {
    setLoading(true);
    try {
      const response = await apiRequest(`/billing/visit/${visitId}`);
      setBilling(response);
      setError(null);
    } catch (err) {
      console.error('Error fetching billing:', err);
      setError('No billing found for this visit');
      setBilling(null);
    } finally {
      setLoading(false);
    }
  };

  const fetchBillingItems = async () => {
    setLoadingItems(true);
    try {
      const response = await apiRequest('/billing/items/active');
      setBillingItems(response);
    } catch (err) {
      console.error('Error fetching billing items:', err);
    } finally {
      setLoadingItems(false);
    }
  };

  /**
   * Handles API errors and extracts the appropriate error message
   * @param err The error object
   * @param defaultMessage Default message to show if error can't be parsed
   * @returns The formatted error message
   */
  const handleApiError = (err: any, defaultMessage: string): string => {
    console.error(defaultMessage, err);
    const errorMessage = err.message || defaultMessage;
    // Extract the actual error message if it's in the format "Error XXX: actual message"
    const match = errorMessage.match(/Error \d+: (.*)/);
    return match ? match[1] : errorMessage;
  };

  const handleAddBillingDetail = async (formData: any) => {
    if (!billing || !selectedBillingItem) return;

    try {
      const requestBody = {
        billingItemId: selectedBillingItem.id,
        amount: formData.amount || detailAmount,
        description: formData.description || detailDescription,
        quantity: formData.quantity || detailQuantity
      };

      await apiRequest(`/billing/${billing.id}/details`, {
        method: 'POST',
        body: requestBody
      });

      // Refresh billing data to get updated information
      await fetchBilling();
      setShowAddItemModal(false);
      resetDetailForm();
      showAlert('Billing item added successfully', 'Success', 'success');
    } catch (err: any) {
      const displayMessage = handleApiError(err, 'Failed to add billing item. Please try again.');
      showAlert(displayMessage, 'Error', 'error');
    }
  };

  const handleUpdateBillingDetail = async (formData: any) => {
    if (!selectedBillingDetail) return;

    try {
      const requestBody = {
        amount: formData.amount || detailAmount,
        description: formData.description || detailDescription,
        quantity: formData.quantity || detailQuantity
      };

      await apiRequest(`/billing/details/${selectedBillingDetail.id}`, {
        method: 'PUT',
        body: requestBody
      });

      // Refresh billing data to get updated information
      await fetchBilling();

      setShowEditItemModal(false);
      resetDetailForm();
      showAlert('Billing item updated successfully', 'Success', 'success');
    } catch (err: any) {
      const displayMessage = handleApiError(err, 'Failed to update billing item. Please try again.');
      showAlert(displayMessage, 'Error', 'error');
    }
  };

  const handleRemoveBillingDetail = async (detailId: number) => {
    if (!billing) return;

    if (!window.confirm('Are you sure you want to remove this item?')) {
      return;
    }

    try {
      await apiRequest(`/billing/details/${detailId}`, {
        method: 'DELETE'
      });

      // Refresh billing data to get updated information
      await fetchBilling();

      showAlert('Billing item removed successfully', 'Success', 'success');
    } catch (err: any) {
      const displayMessage = handleApiError(err, 'Failed to remove billing item. Please try again.');
      showAlert(displayMessage, 'Error', 'error');
    }
  };

  const resetDetailForm = () => {
    setSelectedBillingItem(null);
    setSelectedBillingDetail(null);
    setDetailAmount(0);
    setDetailDescription('');
    setDetailQuantity(1);
  };

  const handleCreateBilling = async () => {
    try {
      await apiRequest('/billing', {
        method: 'POST',
        body: {
          patientVisitId: visitId,
          amount,
          discount,
          totalAmount,
          description
        }
      });

      // Refresh billing data to get updated information
      await fetchBilling();
      setShowCreateModal(false);
      showAlert('Billing created successfully', 'Success', 'success');
    } catch (err: any) {
      const displayMessage = handleApiError(err, 'Failed to create billing. Please try again.');
      showAlert(displayMessage, 'Error', 'error');
    }
  };

  const handleUpdateBilling = async (formData: any) => {
    if (!billing) return;

    try {
      const requestBody = {
        patientVisitId: billing.patientVisitId,
        amount: billing.amount, // Keep the original amount
        discount: formData.discount || discount,
        totalAmount: billing.amount - (formData.discount || discount),
        description: formData.description || description
      };

      await apiRequest(`/billing/${billing.id}`, {
        method: 'PUT',
        body: requestBody
      });

      // Refresh billing data to get updated information
      await fetchBilling();
      setShowEditBillingModal(false);
      showAlert('Billing updated successfully', 'Success', 'success');
    } catch (err: any) {
      const displayMessage = handleApiError(err, 'Failed to update billing. Please try again.');
      showAlert(displayMessage, 'Error', 'error');
    }
  };

  const handleRecordPayment = async (formData: any) => {
    if (!billing) return;

    try {
      // Validate payment amount
      const amount = formData.paymentAmount || paymentAmount;
      if (amount <= 0) {
        showAlert('Payment amount must be greater than zero', 'Validation Error', 'error');
        return;
      }

      if (amount > billing.amountDue) {
        showAlert('Payment amount cannot be greater than amount due', 'Validation Error', 'error');
        return;
      }

      // Use the new BillPaymentController endpoint
      const queryParams = new URLSearchParams({
        amount: amount.toString(),
        paymentMethod: formData.paymentMethod || paymentMethod,
        paymentReference: formData.paymentReference || paymentReference,
        ...(formData.paymentNotes && { notes: formData.paymentNotes })
      });

      await apiRequest(`/billing/payments/${billing.id}?${queryParams.toString()}`, {
        method: 'POST'
      });

      // Refresh billing data to get updated amount due and payment history
      await fetchBilling();

      setShowPaymentModal(false);
      setPaymentMethod('');
      setPaymentReference('');
      setPaymentNotes('');
      showAlert('Payment recorded successfully', 'Success', 'success');
    } catch (err: any) {
      const displayMessage = handleApiError(err, 'Failed to record payment. Please try again.');
      showAlert(displayMessage, 'Error', 'error');
    }
  };

  const handleUpdateStatus = async (status: string) => {
    if (!billing) return;

    try {
      await apiRequest(`/billing/${billing.id}/status?status=${status}`, {
        method: 'PATCH'
      });

      // Refresh billing data to get updated information
      await fetchBilling();
      showAlert(`Billing status updated to ${status}`, 'Status Update', 'info');
    } catch (err: any) {
      const displayMessage = handleApiError(err, 'Failed to update status. Please try again.');
      showAlert(displayMessage, 'Error', 'error');
    }
  };

  if (loading) {
    return <div>Loading billing information...</div>;
  }

  return (
    <Card className="mt-3">
      <Card.Body>
        {error && !billing && (
          <div className="d-flex justify-content-between align-items-center mb-3">
            <div className="text-danger">{error}</div>
            <Button variant="primary" onClick={() => setShowCreateModal(true)}>
              Create Billing
            </Button>
          </div>
        )}

        {billing && (
          <>
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="mb-0">Billing Information</h5>
              <div>
                {billing.status !== 'PAID' && (
                  <>
                    <Button
                      variant="success"
                      className="me-2"
                      onClick={() => setShowPaymentModal(true)}
                    >
                      Record Payment
                    </Button>
                    <Button
                      variant="primary"
                      className="me-2"
                      onClick={() => setShowEditBillingModal(true)}
                    >
                      Edit Billing
                    </Button>
                  </>
                )}
                <Button
                  variant={billing.status === 'CANCELLED' ? 'secondary' : 'danger'}
                  onClick={() => handleUpdateStatus(billing.status === 'CANCELLED' ? 'PENDING' : 'CANCELLED')}
                >
                  {billing.status === 'CANCELLED' ? 'Reactivate' : 'Cancel'}
                </Button>
              </div>
            </div>

            <DataTable
              title="Billing Information"
              columns={[
                { header: 'Field', accessor: 'field' },
                { header: 'Value', accessor: 'value' }
              ]}
              showPagination={false}
              data={[
                { id: 1, field: 'Patient', value: billing.patientName },
                { id: 2, field: 'Amount', value: `${billing.amount?.toFixed(2) || 0.00}` },
                { id: 3, field: 'Discount', value: `${billing.discount?.toFixed(2)|| 0.00}` },
                { id: 4, field: 'Total Amount', value: `${billing.totalAmount?.toFixed(2) || 0.00}` },
                { id: 5, field: 'Amount Due', value: `${billing.amountDue?.toFixed(2) || 0.00}` },
                {
                  id: 6,
                  field: 'Status',
                  value: <span className={`badge bg-${
                    billing.status === 'PAID' ? 'success' : 
                    billing.status === 'PENDING' ? 'warning' : 
                    billing.status === 'CANCELLED' ? 'danger' : 'info'
                  }`}>
                    {billing.status}
                  </span>
                },
                { id: 7, field: 'Description', value: billing.description },
                ...(billing.paidAt ? [
                  { id: 8, field: 'Paid At', value: new Date(billing.paidAt).toLocaleString() }
                ] : [])
              ]}
            />

            <div className="d-flex justify-content-between align-items-center mb-3 mt-4">
              <h5 className="mb-0">Billing Items</h5>
              {billing.status !== 'PAID' && billing.status !== 'CANCELLED' && (
                <Button variant="primary" onClick={() => setShowAddItemModal(true)}>
                  <i className="bi bi-plus-circle me-1"></i> Add Item
                </Button>
              )}
            </div>

            <DataTable
              title="Billing Items"
              columns={[
                { header: 'Item', accessor: 'billingItemName' },
                { header: 'Description', accessor: 'description' },
                { header: 'Quantity', accessor: 'quantity', type: 'number' },
                { header: 'Amount', accessor: 'amount', type: 'currency' },
                { header: 'Total', accessor: 'totalAmount', type: 'currency' }
              ]}
              data={billing.billingDetails || []}
              editButtonAction={billing.status !== 'PAID' && billing.status !== 'CANCELLED' ? (detail) => {
                setSelectedBillingDetail(detail as BillingDetail);
                setDetailAmount(detail.amount);
                setDetailDescription(detail.description);
                setDetailQuantity(detail.quantity);
                setShowEditItemModal(true);
              } : undefined}
              editTitle="Edit"
              editButtonEnabled={billing.status !== 'PAID' && billing.status !== 'CANCELLED'}
              detailsButtonAction={billing.status !== 'PAID' && billing.status !== 'CANCELLED' ? (detail) => {
                handleRemoveBillingDetail(detail.id);
              } : undefined}
              detailsTitle="Remove"
              detailsButtonEnabled={billing.status !== 'PAID' && billing.status !== 'CANCELLED'}
            />

            {/* Payment History Section */}
            <div className="d-flex justify-content-between align-items-center mb-3 mt-4">
              <h5 className="mb-0">Payment History</h5>
            </div>

            <DataTable
              title="Payment History"
              columns={[
                { header: 'Date', accessor: 'paymentDate', type: 'datetime' },
                { header: 'Amount', accessor: 'amount', type: 'currency' },
                { header: 'Method', accessor: 'paymentMethod' },
                { header: 'Reference', accessor: 'paymentReference' },
                { header: 'Notes', accessor: 'notes' }
              ]}
              data={billing.payments || []}
            />
          </>
        )}

        {/* Create Billing Modal */}
        <Modal show={showCreateModal} onHide={() => setShowCreateModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>Create Billing</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <StepForm
              config={[
                {
                  label: "Create Billing",
                  fields: [
                    {
                      name: "amount",
                      label: "Amount",
                      type: "number",
                      required: true,
                      defaultValue: 0,
                      value: amount,
                      onChange: (value) => setAmount(Number(value))
                    },
                    {
                      name: "discount",
                      label: "Discount",
                      type: "number",
                      defaultValue: 0,
                      value: discount,
                      onChange: (value) => setDiscount(Number(value))
                    },
                    {
                      name: "totalAmount",
                      label: "Total Amount",
                      type: "number",
                      value: totalAmount,
                      disabled: true
                    },
                    {
                      name: "description",
                      label: "Description",
                      type: "text",
                      value: description,
                      onChange: (value) => setDescription(value)
                    }
                  ]
                }
              ]}
              onSubmit={handleCreateBilling}
              submitButtonLabel="Create"
              bottomComponents={[
                <Button variant="secondary" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </Button>
              ]}
            />
          </Modal.Body>
        </Modal>

        {/* Record Payment Modal */}
        <Modal show={showPaymentModal} onHide={() => setShowPaymentModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>Record Payment {billing && billing.amountDue > 0 ? `(Amount Due: $${billing.amountDue.toFixed(2)})` : ''}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <StepForm
              config={[
                {
                  label: "Record Payment",
                  fields: [
                    {
                      name: "paymentAmount",
                      label: "Payment Amount",
                      type: "number",
                      required: true,
                      value: paymentAmount,
                      onChange: (value) => setPaymentAmount(Number(value))
                    },
                    {
                      name: "paymentMethod",
                      label: "Payment Method",
                      type: "select",
                      required: true,
                      value: paymentMethod,
                      onChange: (value) => setPaymentMethod(value),
                      options: [
                        { label: "Cash", value: "CASH" },
                        { label: "Credit Card", value: "CREDIT_CARD" },
                        { label: "Debit Card", value: "DEBIT_CARD" },
                        { label: "Insurance", value: "INSURANCE" },
                        { label: "Mobile Money", value: "MOBILE_MONEY" },
                        { label: "Bank Transfer", value: "BANK_TRANSFER" }
                      ]
                    },
                    {
                      name: "paymentReference",
                      label: "Payment Reference",
                      type: "text",
                      required: true,
                      value: paymentReference,
                      onChange: (value) => setPaymentReference(value)
                    },
                    {
                      name: "paymentNotes",
                      label: "Notes",
                      type: "text",
                      value: paymentNotes,
                      onChange: (value) => setPaymentNotes(value)
                    }
                  ]
                }
              ]}
              onSubmit={handleRecordPayment}
              submitButtonLabel={paymentAmount === billing?.amountDue ? "Record Full Payment" : "Record Partial Payment"}
              bottomComponents={[
                <Button variant="secondary" onClick={() => setShowPaymentModal(false)}>
                  Cancel
                </Button>
              ]}
            />
          </Modal.Body>
        </Modal>

        {/* Add Billing Item Modal */}
        <Modal show={showAddItemModal} onHide={() => {
          setShowAddItemModal(false);
          resetDetailForm();
        }}>
          <Modal.Header closeButton>
            <Modal.Title>Add Billing Item</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <StepForm
              config={[
                {
                  label: "Add Billing Item",
                  fields: [
                    {
                      name: "billingItemId",
                      label: "Billing Item",
                      type: "select",
                      required: true,
                      value: selectedBillingItem ? selectedBillingItem.id : '',
                      onChange: (value) => {
                        const itemId = Number(value);
                        const item = billingItems.find(item => item.id === itemId);
                        setSelectedBillingItem(item || null);
                      },
                      options: [
                        ...billingItems.map(item => ({
                          label: `${item.name} ($${item.defaultAmount?.toFixed(2)})`,
                          value: item.id
                        }))
                      ]
                    },
                    {
                      name: "amount",
                      label: "Unit Amount",
                      type: "number",
                      required: true,
                      value: detailAmount,
                      onChange: (value) => setDetailAmount(Number(value))
                    },
                    {
                      name: "quantity",
                      label: "Quantity",
                      type: "number",
                      required: true,
                      value: detailQuantity,
                      onChange: (value) => setDetailQuantity(Number(value))
                    },
                    {
                      name: "description",
                      label: "Description",
                      type: "text",
                      value: detailDescription,
                      onChange: (value) => setDetailDescription(value)
                    }
                  ]
                }
              ]}
              onSubmit={handleAddBillingDetail}
              submitButtonLabel="Add Item"
              bottomComponents={[
                <Button variant="secondary" onClick={() => {
                  setShowAddItemModal(false);
                  resetDetailForm();
                }}>
                  Cancel
                </Button>
              ]}
            />
          </Modal.Body>
        </Modal>

        {/* Edit Billing Item Modal */}
        <Modal show={showEditItemModal} onHide={() => {
          setShowEditItemModal(false);
          resetDetailForm();
        }}>
          <Modal.Header closeButton>
            <Modal.Title>Edit Billing Item</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <StepForm
              config={[
                {
                  label: "Edit Billing Item",
                  fields: [
                    {
                      name: "billingItemName",
                      label: "Billing Item",
                      type: "text",
                      value: selectedBillingDetail ? selectedBillingDetail.billingItemName : '',
                      defaultValue: selectedBillingDetail ? selectedBillingDetail.billingItemName : '',
                      disabled: true
                    },
                    {
                      name: "amount",
                      label: "Amount",
                      type: "number",
                      required: true,
                      value: detailAmount,
                      defaultValue: detailAmount,
                      onChange: (value) => setDetailAmount(Number(value))
                    },
                    {
                      name: "quantity",
                      label: "Quantity",
                      type: "number",
                      required: true,
                      value: detailQuantity,
                      defaultValue: detailQuantity,
                      onChange: (value) => setDetailQuantity(Number(value))
                    },
                    {
                      name: "description",
                      label: "Description",
                      type: "text",
                      value: detailDescription,
                      defaultValue: detailDescription,
                      onChange: (value) => setDetailDescription(value)
                    }
                  ]
                }
              ]}
              onSubmit={handleUpdateBillingDetail}
              submitButtonLabel="Update Item"
              bottomComponents={[
                <Button variant="secondary" onClick={() => {
                  setShowEditItemModal(false);
                  resetDetailForm();
                }}>
                  Cancel
                </Button>
              ]}
            />
          </Modal.Body>
        </Modal>

        {/* Edit Billing Modal */}
        <Modal show={showEditBillingModal} onHide={() => setShowEditBillingModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>Edit Billing</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <StepForm
              config={[
                {
                  label: "Edit Billing",
                  fields: [
                    {
                      name: "amount",
                      label: "Amount",
                      type: "number",
                      value: billing ? billing.amount : 0,
                      defaultValue: billing ? billing.amount : 0,
                      disabled: true // Amount is calculated from billing details
                    },
                    {
                      name: "discount",
                      label: "Discount",
                      type: "number",
                      required: true,
                      value: billing ? billing.discount : 0,
                      defaultValue: billing ? billing.discount : 0,
                      onChange: (value) => setDiscount(Number(value))
                    },
                    {
                      name: "totalAmount",
                      label: "Total Amount",
                      type: "number",
                      value: billing ? (billing.amount - (billing.discount || 0)) : 0,
                      defaultValue: billing ? (billing.amount - (billing.discount || 0)) : 0,
                      disabled: true // Total amount is calculated
                    },
                    {
                      name: "description",
                      label: "Description",
                      type: "text",
                      value: billing ? billing.description : '',
                      defaultValue: billing ? billing.description : '',
                      onChange: (value) => setDescription(value)
                    }
                  ]
                }
              ]}
              onSubmit={handleUpdateBilling}
              submitButtonLabel="Update Billing"
              bottomComponents={[
                <Button variant="secondary" onClick={() => setShowEditBillingModal(false)}>
                  Cancel
                </Button>
              ]}
            />
          </Modal.Body>
        </Modal>
      </Card.Body>
    </Card>
  );
};

export default BillingComponent;
