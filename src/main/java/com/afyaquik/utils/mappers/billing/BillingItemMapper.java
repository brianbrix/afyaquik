package com.afyaquik.utils.mappers.billing;

import com.afyaquik.billing.dto.BillingItemDto;
import com.afyaquik.billing.entity.BillingItem;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillingItemMapper extends EntityMapper<BillingItem, BillingItemDto> {
    @Override
    @Mapping(source = "currency.id", target = "currencyId")
    @Mapping(source = "currency.code", target = "currencyCode")
    @Mapping(source = "currency.symbol", target = "currencySymbol")
    BillingItemDto toDto(BillingItem entity);
}
