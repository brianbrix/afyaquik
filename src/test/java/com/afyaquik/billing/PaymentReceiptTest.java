package com.afyaquik.billing;

import com.afyaquik.billing.entity.*;
import com.afyaquik.billing.repository.*;
import com.afyaquik.billing.service.impl.BillPaymentServiceImpl;
import com.afyaquik.patients.entity.*;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.utils.mappers.billing.BillPaymentMapper;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentReceiptTest {
    private final BillPaymentRepository payments = mock(BillPaymentRepository.class);
    private final BillingRepository bills = mock(BillingRepository.class);
    private final BillPaymentServiceImpl service = new BillPaymentServiceImpl(payments, bills, mock(BillPaymentMapper.class));
    private final Billing bill = Billing.builder().id(1L).currencyCode("KES").totalAmount(new BigDecimal("1000.00"))
            .patientVisit(PatientVisit.builder().id(2L).patient(Patient.builder().id(3L).firstName("Synthetic").lastName("Patient").build()).build()).build();

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("cashier", null,
                List.of(new SimpleGrantedAuthority("ROLE_CASHIER"))));
        when(bills.findForUpdate(1L)).thenReturn(Optional.of(bill));
        when(payments.save(any())).thenAnswer(invocation -> {
            BillPayment payment = invocation.getArgument(0);
            payment.setId(10L);
            return payment;
        });
    }

    @AfterEach
    void clear() { SecurityContextHolder.clearContext(); }

    @Test
    void receiptKeepsOriginalBalancesCurrencyAndIdentityAfterLaterChanges() {
        service.createPayment(1L, new BigDecimal("600.00"), "cash", "test-first", null);
        BillPayment payment = bill.getPayments().get(0);
        when(payments.findById(10L)).thenReturn(Optional.of(payment));
        bill.setCurrencyCode("USD");
        bill.getPatientVisit().getPatient().setFirstName("Changed");
        bill.addPayment(BillPayment.builder().amount(new BigDecimal("400.00")).build());
        var receipt = service.getReceipt(10L);
        assertEquals("KES", receipt.currencyCode());
        assertEquals("Synthetic Patient", receipt.patientName());
        assertEquals(new BigDecimal("400.00"), receipt.balance());
        assertEquals(new BigDecimal("600.00"), receipt.paidToDate());
        assertTrue(receipt.historicalSnapshot());
    }

    @Test
    void retryingTheSameReferenceDoesNotDoubleCharge() {
        service.createPayment(1L, new BigDecimal("600.00"), "CASH", "test-first", null);
        when(payments.findByBillingIdAndPaymentMethodIgnoreCaseAndPaymentReferenceIgnoreCase(1L, "CASH", "test-first"))
                .thenReturn(Optional.of(bill.getPayments().get(0)));
        service.createPayment(1L, new BigDecimal("600.00"), "CASH", "test-first", null);
        assertEquals(1, bill.getPayments().size());
        verify(payments, times(1)).save(any());
    }

    @Test
    void reversalRetainsOriginalPaymentAndRestoresBalance() {
        service.createPayment(1L, new BigDecimal("1000.00"), "MPESA", "test-transaction", null);
        BillPayment payment = bill.getPayments().get(0);
        when(payments.findForUpdate(10L)).thenReturn(Optional.of(payment));
        service.reversePayment(10L, "Duplicate collection corrected");
        assertEquals(1, bill.getPayments().size());
        assertTrue(payment.isReversed());
        assertEquals(new BigDecimal("1000.00"), bill.getAmountDue());
        assertEquals(Status.PENDING, bill.getStatus());
        verify(payments, never()).delete(any());
    }

    @Test
    void cancelledBillsAndFractionsOfACentCannotReceivePayments() {
        bill.setStatus(Status.CANCELLED);
        assertThrows(IllegalArgumentException.class, () -> service.createPayment(1L, BigDecimal.ONE, "CASH", "test", null));
        assertThrows(IllegalArgumentException.class, () -> service.createPayment(1L, new BigDecimal("1.001"), "CASH", "test", null));
        verify(payments, never()).save(any());
    }
}