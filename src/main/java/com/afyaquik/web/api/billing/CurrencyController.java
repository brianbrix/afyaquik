package com.afyaquik.web.api.billing;

import com.afyaquik.billing.dto.CurrencyDto;
import com.afyaquik.billing.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    public ResponseEntity<CurrencyDto> createCurrency(@RequestBody CurrencyDto currencyDto) {
        return new ResponseEntity<>(currencyService.createCurrency(currencyDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurrencyDto> updateCurrency(@PathVariable Long id, @RequestBody CurrencyDto currencyDto) {
        return ResponseEntity.ok(currencyService.updateCurrency(id, currencyDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurrencyDto> getCurrencyById(@PathVariable Long id) {
        return ResponseEntity.ok(currencyService.getCurrencyById(id));
    }

    @GetMapping
    public ResponseEntity<List<CurrencyDto>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @GetMapping("/active")
    public ResponseEntity<CurrencyDto> getActiveCurrency() {
        CurrencyDto activeCurrency = currencyService.getActiveCurrency();
        if (activeCurrency == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(activeCurrency);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CurrencyDto> setActiveCurrency(@PathVariable Long id) {
        return ResponseEntity.ok(currencyService.setActiveCurrency(id));
    }
}
