package com.afyaquik.billing;

import com.afyaquik.billing.entity.BillPayment;
import com.afyaquik.billing.entity.Billing;
import com.afyaquik.patients.enums.Status;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BillingBalanceTest {
    @Test
    void removingInstallmentReopensPreviouslyPaidBill() {
        Billing bill = Billing.builder().totalAmount(new BigDecimal("1000.00")).status(Status.PENDING).build();
        BillPayment first = BillPayment.builder().amount(new BigDecimal("600.00")).build();
        BillPayment second = BillPayment.builder().amount(new BigDecimal("400.00")).build();
        bill.addPayment(first);
        bill.addPayment(second);
        assertEquals(Status.PAID, bill.getStatus());
        bill.removePayment(second);
        assertEquals(Status.PENDING, bill.getStatus());
        assertEquals(new BigDecimal("400.00"), bill.getAmountDue());
        assertNull(bill.getPaidAt());
    }

    @Test
    void nonPositivePaymentsCannotReduceTheLedgerBalance() {
        Billing bill = Billing.builder().totalAmount(new BigDecimal("1000.00")).build();
        assertThrows(IllegalArgumentException.class, () -> bill.addPayment(BillPayment.builder().amount(BigDecimal.ZERO).build()));
        assertThrows(IllegalArgumentException.class, () -> bill.addPayment(BillPayment.builder().amount(new BigDecimal("-1.00")).build()));
    }
}