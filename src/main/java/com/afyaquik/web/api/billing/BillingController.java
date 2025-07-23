package com.afyaquik.web.api.billing;

import com.afyaquik.billing.dto.BillingDetailDto;
import com.afyaquik.billing.dto.BillingDto;
import com.afyaquik.billing.service.BillingService;
import com.afyaquik.patients.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<BillingDto> createBilling(@RequestBody BillingDto billingDto) {
        return new ResponseEntity<>(billingService.createBilling(billingDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingDto> updateBilling(@PathVariable Long id, @RequestBody BillingDto billingDto) {
        return ResponseEntity.ok(billingService.updateBilling(id, billingDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingDto> getBillingById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillingById(id));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<BillingDto> getBillingByVisitId(@PathVariable Long visitId) {
        return ResponseEntity.ok(billingService.getBillingByPatientVisitId(visitId));
    }

    @GetMapping
    public ResponseEntity<List<BillingDto>> getAllBillings() {
        return ResponseEntity.ok(billingService.getAllBillings());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillingDto>> getBillingsByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(billingService.getBillingsByStatus(status));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillingDto>> getBillingsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(billingService.getBillingsByPatientId(patientId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BillingDto> updateBillingStatus(@PathVariable Long id, @RequestParam Status status) {
        return ResponseEntity.ok(billingService.updateBillingStatus(id, status));
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<BillingDto> recordPayment(
            @PathVariable Long id,
            @RequestParam String paymentMethod,
            @RequestParam String paymentReference) {
        return ResponseEntity.ok(billingService.recordPayment(id, paymentMethod, paymentReference));
    }

    @PostMapping("/{id}/details")
    public ResponseEntity<BillingDetailDto> addBillingDetail(
            @PathVariable Long id,
            @RequestBody BillingDetailDto billingDetailDto) {
        return ResponseEntity.ok(billingService.addBillingDetail(id, billingDetailDto));
    }

    @PutMapping("/details/{detailId}")
    public ResponseEntity<BillingDetailDto> updateBillingDetail(
            @PathVariable Long detailId,
            @RequestBody BillingDetailDto billingDetailDto) {
        return ResponseEntity.ok(billingService.updateBillingDetail(detailId, billingDetailDto));
    }

    @DeleteMapping("/details/{detailId}")
    public ResponseEntity<Void> removeBillingDetail(@PathVariable Long detailId) {
        billingService.removeBillingDetail(detailId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<List<BillingDetailDto>> getBillingDetailsByBillingId(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillingDetailsByBillingId(id));
    }
}
