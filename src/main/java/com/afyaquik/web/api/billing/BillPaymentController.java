package com.afyaquik.web.api.billing;

import com.afyaquik.billing.dto.BillPaymentDto;
import com.afyaquik.billing.service.BillPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/billing/payments")
@RequiredArgsConstructor
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    @PostMapping("/{billingId}")
    public ResponseEntity<BillPaymentDto> createPayment(
            @PathVariable Long billingId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod,
            @RequestParam String paymentReference,
            @RequestParam(required = false) String notes) {
        return new ResponseEntity<>(
                billPaymentService.createPayment(billingId, amount, paymentMethod, paymentReference, notes),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillPaymentDto> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(billPaymentService.getPaymentById(id));
    }

    @GetMapping("/billing/{billingId}")
    public ResponseEntity<List<BillPaymentDto>> getPaymentsByBillingId(@PathVariable Long billingId) {
        return ResponseEntity.ok(billPaymentService.getPaymentsByBillingId(billingId));
    }

    @GetMapping("/visit/{patientVisitId}")
    public ResponseEntity<List<BillPaymentDto>> getPaymentsByPatientVisitId(@PathVariable Long patientVisitId) {
        return ResponseEntity.ok(billPaymentService.getPaymentsByPatientVisitId(patientVisitId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        billPaymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
