package com.afyaquik.billing.config;

import com.afyaquik.billing.entity.Currency;
import com.afyaquik.billing.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CurrencyDataLoader {

    private final CurrencyRepository currencyRepository;

    @Bean
    @Transactional
    public CommandLineRunner loadCurrencies() {
        return args -> {
            // Only run if no currencies exist
            if (currencyRepository.count() == 0) {
                // Create KES (Kenyan Shilling) as default active currency
                Currency kes = Currency.builder()
                        .name("Kenyan Shilling")
                        .code("KES")
                        .symbol("KSh")
                        .active(true)
                        .build();

                // Create other common currencies (inactive by default)
                Currency usd = Currency.builder()
                        .name("US Dollar")
                        .code("USD")
                        .symbol("$")
                        .active(false)
                        .build();

                Currency eur = Currency.builder()
                        .name("Euro")
                        .code("EUR")
                        .symbol("€")
                        .active(false)
                        .build();

                Currency gbp = Currency.builder()
                        .name("British Pound")
                        .code("GBP")
                        .symbol("£")
                        .active(false)
                        .build();

                // Save all currencies
                currencyRepository.saveAll(List.of(kes, usd, eur, gbp));
                
                System.out.println("Default currencies initialized with KES as active currency");
            }
        };
    }
}
