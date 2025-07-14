package com.afyaquik.web.api.billing;

import com.afyaquik.billing.dto.BillingItemDto;
import com.afyaquik.billing.service.BillingItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing/items")
@RequiredArgsConstructor
public class BillingItemController {

    private final BillingItemService billingItemService;

    @PostMapping
    public ResponseEntity<BillingItemDto> createBillingItem(@RequestBody BillingItemDto billingItemDto) {
        return new ResponseEntity<>(billingItemService.createBillingItem(billingItemDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingItemDto> updateBillingItem(@PathVariable Long id, @RequestBody BillingItemDto billingItemDto) {
        return ResponseEntity.ok(billingItemService.updateBillingItem(id, billingItemDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingItemDto> getBillingItemById(@PathVariable Long id) {
        return ResponseEntity.ok(billingItemService.getBillingItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<BillingItemDto>> getAllBillingItems() {
        return ResponseEntity.ok(billingItemService.getAllBillingItems());
    }

    @GetMapping("/active")
    public ResponseEntity<List<BillingItemDto>> getAllActiveBillingItems() {
        return ResponseEntity.ok(billingItemService.getAllActiveBillingItems());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BillingItemDto>> searchBillingItemsByName(@RequestParam String name) {
        return ResponseEntity.ok(billingItemService.searchBillingItemsByName(name));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<BillingItemDto> setActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(billingItemService.setActive(id, active));
    }
}
