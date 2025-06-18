package com.afyaquik.utils.mappers.pharmacy;

import com.afyaquik.pharmacy.dto.DrugDto;
import com.afyaquik.pharmacy.entity.Drug;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DrugMapper extends EntityMapper<Drug, DrugDto> {
    @Override
     default DrugDto toDto(Drug entity){
        return DrugDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .strength(entity.getStrength())
                .manufacturer(entity.getManufacturer())
                .sampleDosageInstruction(entity.getSampleDosageInstruction())
                .brandName(entity.getBrandName())
                .stockQuantity(entity.getStockQuantity())
                .isPrescriptionRequired(entity.isPrescriptionRequired())
                .atcCode(entity.getAtcCode())
                .drugFormId(entity.getDrugForm().getId())
                .drugFormName(entity.getDrugForm().getName())
                .categoryId(entity.getDrugCategory().getId())
                .categoryName(entity.getDrugCategory().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
