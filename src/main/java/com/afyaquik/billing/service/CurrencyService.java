package com.afyaquik.billing.service;

import com.afyaquik.billing.dto.CurrencyDto;

import java.util.List;

public interface CurrencyService {

    /**
     * Create a new currency
     * @param currencyDto the currency data
     * @return the created currency
     */
    CurrencyDto createCurrency(CurrencyDto currencyDto);

    /**
     * Update an existing currency
     * @param id the currency id
     * @param currencyDto the updated currency data
     * @return the updated currency
     */
    CurrencyDto updateCurrency(Long id, CurrencyDto currencyDto);

    /**
     * Get a currency by id
     * @param id the currency id
     * @return the currency
     */
    CurrencyDto getCurrencyById(Long id);

    /**
     * Get all currencies
     * @return list of all currencies
     */
    List<CurrencyDto> getAllCurrencies();

    /**
     * Get the active currency
     * @return the active currency, or null if none is active
     */
    CurrencyDto getActiveCurrency();

    /**
     * Set a currency as active, deactivating all others
     * @param id the currency id to set as active
     * @return the activated currency
     */
    CurrencyDto setActiveCurrency(Long id);
}
