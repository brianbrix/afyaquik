package com.afyaquik.billing.service;

import com.afyaquik.billing.dto.BillingItemDto;

import java.util.List;

public interface BillingItemService {

    /**
     * Create a new billing item
     * @param billingItemDto the billing item data
     * @return the created billing item
     */
    BillingItemDto createBillingItem(BillingItemDto billingItemDto);

    /**
     * Update an existing billing item
     * @param id the billing item id
     * @param billingItemDto the updated billing item data
     * @return the updated billing item
     */
    BillingItemDto updateBillingItem(Long id, BillingItemDto billingItemDto);

    /**
     * Get a billing item by id
     * @param id the billing item id
     * @return the billing item
     */
    BillingItemDto getBillingItemById(Long id);

    /**
     * Get all billing items
     * @return list of all billing items
     */
    List<BillingItemDto> getAllBillingItems();

    /**
     * Get all active billing items
     * @return list of all active billing items
     */
    List<BillingItemDto> getAllActiveBillingItems();

    /**
     * Search for billing items by name
     * @param name the name to search for
     * @return list of billing items matching the search criteria
     */
    List<BillingItemDto> searchBillingItemsByName(String name);

    /**
     * Activate or deactivate a billing item
     * @param id the billing item id
     * @param active true to activate, false to deactivate
     * @return the updated billing item
     */
    BillingItemDto setActive(Long id, boolean active);
}
