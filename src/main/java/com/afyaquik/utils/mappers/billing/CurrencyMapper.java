package com.afyaquik.utils.mappers.billing;

import com.afyaquik.billing.dto.CurrencyDto;
import com.afyaquik.billing.entity.Currency;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper extends EntityMapper<Currency, CurrencyDto> {
    @Override
    CurrencyDto toDto(Currency entity);
}
