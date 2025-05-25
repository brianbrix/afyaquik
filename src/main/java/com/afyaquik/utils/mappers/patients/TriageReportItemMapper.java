package com.afyaquik.utils.mappers.patients;


import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.TriageReportItemDto;
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
