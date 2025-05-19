package com.afyaquik.appointments.mappers;


import com.afyaquik.appointments.entity.Appointment;
import com.afyaquik.appointments.enums.AppointmentStatus;
import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.dtos.appointments.AppointmentDto;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface AppointmentMapper extends EntityMapper<Appointment, AppointmentDto> {
    @Override
    AppointmentDto toDto(Appointment entity);

    default Appointment toEntity(AppointmentDto dto) {
        return Appointment.builder()
                .id(dto.getId())
                .appointmentDateTime(dto.getAppointmentDateTime())
                .reason(dto.getReason())
                .status(AppointmentStatus.valueOf(dto.getStatus()))
                .build();
    }
}

