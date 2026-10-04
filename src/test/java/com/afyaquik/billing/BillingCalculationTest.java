package com.afyaquik.billing;

import com.afyaquik.billing.dto.*;
import com.afyaquik.billing.entity.*;
import com.afyaquik.billing.repository.*;
import com.afyaquik.billing.service.BillPaymentService;
import com.afyaquik.billing.service.impl.BillingServiceImpl;
import com.afyaquik.patients.entity.*;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.utils.mappers.billing.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillingCalculationTest {
    private final BillingRepository bills = mock(BillingRepository.class);
    private final PatientVisitRepo visits = mock(PatientVisitRepo.class);
    private final CurrencyRepository currencies = mock(CurrencyRepository.class);
    private final BillingItemRepository items = mock(BillingItemRepository.class);
    private final BillingServiceImpl service = new BillingServiceImpl(bills, visits, items, mock(BillingDetailRepository.class),
            mock(BillingMapper.class), mock(BillingDetailMapper.class), currencies, mock(BillPaymentService.class));

    @Test
    void clientTotalCannotOverrideInvoiceArithmetic() {
        when(visits.findForUpdate(1L)).thenReturn(Optional.of(PatientVisit.builder().patient(Patient.builder().build()).build()));
        when(currencies.findByActiveTrue()).thenReturn(Optional.of(Currency.builder().code("KES").build()));
        when(bills.save(any())).thenAnswer(invocation -> {
            Billing bill = invocation.getArgument(0);
            assertEquals(new BigDecimal("900.00"), bill.getTotalAmount());
            assertEquals("KES", bill.getCurrencyCode());
            return bill;
        });
        service.createBilling(BillingDto.builder().patientVisitId(1L).amount(new BigDecimal("1000")).discount(new BigDecimal("100"))
                .totalAmount(BigDecimal.ONE).build());
        verify(bills).save(any());
    }

    @Test
    void statusEditCannotManufacturePayment() {
        when(bills.findForUpdate(1L)).thenReturn(Optional.of(Billing.builder().build()));
        assertThrows(IllegalArgumentException.class, () -> service.updateBillingStatus(1L, Status.PAID));
        verify(bills, never()).save(any());
    }

    @Test
    void addingItemsPreservesOpeningChargeAndAppliesDiscount() {
        Billing bill = Billing.builder().currencyCode("KES").amount(new BigDecimal("500"))
                .openingAmount(new BigDecimal("500")).discount(new BigDecimal("50")).build();
        when(bills.findForUpdate(1L)).thenReturn(Optional.of(bill));
        when(items.findById(2L)).thenReturn(Optional.of(BillingItem.builder().active(true).currency(Currency.builder().code("KES").build())
                .defaultAmount(new BigDecimal("100")).build()));
        service.addBillingDetail(1L, BillingDetailDto.builder().billingItemId(2L).quantity(2).build());
        assertEquals(new BigDecimal("650.00"), bill.getTotalAmount());
    }

    @Test
    void issuedInvoicesCannotBeRepriced() {
        Billing bill = Billing.builder().build();
        bill.getPayments().add(BillPayment.builder().build());
        when(bills.findForUpdate(1L)).thenReturn(Optional.of(bill));
        assertThrows(IllegalArgumentException.class, () -> service.updateBilling(1L, BillingDto.builder().build()));
    }
}