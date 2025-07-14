package com.afyaquik.billing.service.impl;

import com.afyaquik.billing.dto.CurrencyDto;
import com.afyaquik.billing.entity.Currency;
import com.afyaquik.billing.repository.CurrencyRepository;
import com.afyaquik.billing.service.CurrencyService;
import com.afyaquik.utils.mappers.billing.CurrencyMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override
    @Transactional
    public CurrencyDto createCurrency(CurrencyDto currencyDto) {
        // Check if this is the first currency being created
        boolean isFirstCurrency = currencyRepository.count() == 0;

        Currency currency = Currency.builder()
                .name(currencyDto.getName())
                .code(currencyDto.getCode())
                .symbol(currencyDto.getSymbol())
                .active(isFirstCurrency || currencyDto.isActive()) // First currency is automatically active
                .build();

        // If this currency is to be active, deactivate all others
        if (currency.isActive()) {
            deactivateAllCurrencies();
        }

        Currency savedCurrency = currencyRepository.save(currency);
        return currencyMapper.toDto(savedCurrency);
    }

    @Override
    @Transactional
    public CurrencyDto updateCurrency(Long id, CurrencyDto currencyDto) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));

        currency.setName(currencyDto.getName());
        currency.setCode(currencyDto.getCode());
        currency.setSymbol(currencyDto.getSymbol());

        // If this currency is to be active and it's not already active, deactivate all others
        if (currencyDto.isActive() && !currency.isActive()) {
            deactivateAllCurrencies();
            currency.setActive(true);
        } else if (!currencyDto.isActive() && currency.isActive()) {
            // If trying to deactivate the only active currency, prevent it
            if (currencyRepository.count() > 1) {
                currency.setActive(false);
            } else {
                // Don't allow deactivating the only currency
                throw new IllegalStateException("Cannot deactivate the only currency. At least one currency must be active.");
            }
        }

        Currency updatedCurrency = currencyRepository.save(currency);
        return currencyMapper.toDto(updatedCurrency);
    }

    @Override
    public CurrencyDto getCurrencyById(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));
        return currencyMapper.toDto(currency);
    }

    @Override
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(currencyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CurrencyDto getActiveCurrency() {
        return currencyRepository.findByActiveTrue()
                .map(currencyMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public CurrencyDto setActiveCurrency(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));

        // Deactivate all currencies
        deactivateAllCurrencies();

        // Activate the specified currency
        currency.setActive(true);
        Currency activatedCurrency = currencyRepository.save(currency);

        return currencyMapper.toDto(activatedCurrency);
    }

    private void deactivateAllCurrencies() {
        List<Currency> activeCurrencies = currencyRepository.findAll().stream()
                .filter(Currency::isActive)
                .toList();
        for (Currency currency : activeCurrencies) {
            currency.setActive(false);
            currencyRepository.save(currency);
        }
    }
}
