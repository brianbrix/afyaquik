package com.afyaquik.utils.mappers.billing;

import com.afyaquik.billing.dto.BillingDetailDto;
import com.afyaquik.billing.entity.BillingDetail;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillingDetailMapper extends EntityMapper<BillingDetail, BillingDetailDto> {

    @Override
    @Mapping(source = "billing.id", target = "billingId")
    @Mapping(source = "billingItem.id", target = "billingItemId")
    @Mapping(source = "billingItem.name", target = "billingItemName")
    BillingDetailDto toDto(BillingDetail entity);
}
