package com.afyaquik.utils.mappers.billing;

import com.afyaquik.billing.dto.BillPaymentDto;
import com.afyaquik.billing.entity.BillPayment;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillPaymentMapper extends EntityMapper<BillPayment, BillPaymentDto> {

    @Override
    @Mapping(source = "billing.id", target = "billingId")
    BillPaymentDto toDto(BillPayment entity);
}
