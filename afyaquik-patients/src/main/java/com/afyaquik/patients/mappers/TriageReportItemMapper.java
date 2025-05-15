package com.afyaquik.patients.mappers;


import com.afyaquik.core.mappers.EntityMapper;
import com.afyaquik.dtos.patient.TriageReportItemDto;
import com.afyaquik.patients.entity.TriageReportItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TriageReportItemMapper extends EntityMapper<TriageReportItem, TriageReportItemDto> {
    default TriageReportItemDto toDto(TriageReportItem triageReportItem){
        return TriageReportItemDto.builder()
                .name(triageReportItem.getTriageItem().getName())
                .value(triageReportItem.getItemSummary())
                .build();
    }

}
