package com.afyaquik.billing.service.impl;

import com.afyaquik.billing.dto.BillingItemDto;
import com.afyaquik.billing.entity.BillingItem;
import com.afyaquik.billing.entity.Currency;
import com.afyaquik.billing.repository.BillingItemRepository;
import com.afyaquik.billing.repository.CurrencyRepository;
import com.afyaquik.billing.service.BillingItemService;
import com.afyaquik.utils.mappers.billing.BillingItemMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingItemServiceImpl implements BillingItemService {

    private final BillingItemRepository billingItemRepository;
    private final BillingItemMapper billingItemMapper;
    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional
    public BillingItemDto createBillingItem(BillingItemDto billingItemDto) {
        // Get the currency based on the currencyId in the DTO, or use the active currency if none is specified
        Currency currency;
        if (billingItemDto.getCurrencyId() != null) {
            currency = currencyRepository.findById(billingItemDto.getCurrencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + billingItemDto.getCurrencyId()));
        } else {
            currency = currencyRepository.findByActiveTrue()
                    .orElseThrow(() -> new IllegalStateException("No active currency found. Please set an active currency first."));
        }

        BillingItem billingItem = BillingItem.builder()
                .name(billingItemDto.getName())
                .description(billingItemDto.getDescription())
                .defaultAmount(billingItemDto.getDefaultAmount())
                .active(true)
                .currency(currency)
                .build();

        BillingItem savedBillingItem = billingItemRepository.save(billingItem);
        return billingItemMapper.toDto(savedBillingItem);
    }

    @Override
    @Transactional
    public BillingItemDto updateBillingItem(Long id, BillingItemDto billingItemDto) {
        BillingItem billingItem = billingItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing item not found with id: " + id));

        billingItem.setName(billingItemDto.getName());
        billingItem.setDescription(billingItemDto.getDescription());
        billingItem.setDefaultAmount(billingItemDto.getDefaultAmount());

        // Update currency if a new currencyId is provided
        if (billingItemDto.getCurrencyId() != null) {
            Currency currency = currencyRepository.findById(billingItemDto.getCurrencyId())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + billingItemDto.getCurrencyId()));
            billingItem.setCurrency(currency);
        }

        BillingItem updatedBillingItem = billingItemRepository.save(billingItem);
        return billingItemMapper.toDto(updatedBillingItem);
    }

    @Override
    public BillingItemDto getBillingItemById(Long id) {
        BillingItem billingItem = billingItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing item not found with id: " + id));
        return billingItemMapper.toDto(billingItem);
    }

    @Override
    public List<BillingItemDto> getAllBillingItems() {
        return billingItemRepository.findAll().stream()
                .map(billingItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingItemDto> getAllActiveBillingItems() {
        return billingItemRepository.findByActiveTrue().stream()
                .map(billingItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingItemDto> searchBillingItemsByName(String name) {
        return billingItemRepository.findByNameContainingIgnoreCase(name).stream()
                .map(billingItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BillingItemDto setActive(Long id, boolean active) {
        BillingItem billingItem = billingItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billing item not found with id: " + id));

        billingItem.setActive(active);

        BillingItem updatedBillingItem = billingItemRepository.save(billingItem);
        return billingItemMapper.toDto(updatedBillingItem);
    }
}
