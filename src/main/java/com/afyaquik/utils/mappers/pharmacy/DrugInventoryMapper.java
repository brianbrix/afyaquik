package com.afyaquik.utils.mappers.pharmacy;

import com.afyaquik.pharmacy.dto.DrugInventoryDto;
import com.afyaquik.pharmacy.entity.DrugInventory;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DrugInventoryMapper extends EntityMapper<DrugInventory, DrugInventoryDto> {
    @Override
    default DrugInventoryDto toDto(DrugInventory entity) {
        return DrugInventoryDto.builder()
                .id(entity.getId())
                .drugId(entity.getDrug().getId())
                .initialQuantity(entity.getInitialQuantity())
                .currentQuantity(entity.getCurrentQuantity())
                .drugName(entity.getDrug().getName())
                .batchNumber(entity.getBatchNumber())
                .receivedDate(entity.getReceivedDate())
                .sellingPrice(entity.getSellingPrice())
                .buyingPrice(entity.getBuyingPrice())
                .supplierName(entity.getSupplierName())
                .expiryDate(entity.getExpiryDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    DrugInventory toEntity(DrugInventoryDto inventoryDto);
}
