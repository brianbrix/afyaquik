package com.afyaquik.utils.mappers.billing;

import com.afyaquik.utils.mappers.MapperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillingMapperRegistry {

    @Autowired
    public BillingMapperRegistry(BillingMapper billingMapper, BillingDetailMapper billingDetailMapper,BillingItemMapper billingItemMapper,CurrencyMapper currencyMapper, MapperRegistry mapperRegistry) {
        {
           mapperRegistry.registerMapper("billingItems", billingItemMapper);
           mapperRegistry.registerMapper("billingDetails", billingDetailMapper);
           mapperRegistry.registerMapper("billings", billingMapper);
           mapperRegistry.registerMapper("currencies", currencyMapper);
        }
    }
}
