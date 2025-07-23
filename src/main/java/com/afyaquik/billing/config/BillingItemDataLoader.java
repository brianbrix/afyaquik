package com.afyaquik.billing.config;

import com.afyaquik.billing.entity.BillingItem;
import com.afyaquik.billing.entity.Currency;
import com.afyaquik.billing.repository.BillingItemRepository;
import com.afyaquik.billing.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BillingItemDataLoader {

    private final BillingItemRepository billingItemRepository;
    private final CurrencyRepository currencyRepository;

    @Bean
    public CommandLineRunner loadBillingItems() {
        return args -> {
            // Check if Pharmacy billing item already exists
            if (billingItemRepository.findByNameIgnoreCase("Pharmacy").isEmpty()) {
                // Get the active currency
                Currency currency = currencyRepository.findByActiveTrue()
                        .orElseThrow(() -> new IllegalStateException("No active currency found. Please set an active currency first."));

                // Create the Pharmacy billing item
                BillingItem pharmacyItem = BillingItem.builder()
                        .name("Pharmacy")
                        .description("Billing item for pharmacy drugs")
                        .defaultAmount(BigDecimal.ZERO) // Default amount is zero, will be set based on actual drugs
                        .active(true)
                        .currency(currency)
                        .build();

                billingItemRepository.save(pharmacyItem);
                System.out.println("Pharmacy billing item created");
            }
        };
    }
}
