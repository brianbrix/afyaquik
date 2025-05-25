package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.doctor.dto.ObservationReportItemDto;
import com.afyaquik.doctor.entity.ObservationReportItem;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObservationReportItemMapper extends EntityMapper<ObservationReportItem, ObservationReportItemDto> {
    default ObservationReportItemDto toDto(ObservationReportItem observationReportItem) {
        return ObservationReportItemDto.builder()
                .id(observationReportItem.getId())
                .itemName(observationReportItem.getItem().getName())
                .itemId(observationReportItem.getItem().getId())
                .reportId(observationReportItem.getObservationReport().getId())
                .value(observationReportItem.getValue())
                .build();
    }

}
