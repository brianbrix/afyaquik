package com.afyaquik.appointments.mappers;


import com.afyaquik.appointments.entity.Appointment;
import com.afyaquik.appointments.enums.AppointmentStatus;
import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.appointments.dto.AppointmentDto;
import com.afyaquik.utils.mappers.patients.PatientMapper;
import com.afyaquik.utils.mappers.users.UserMapper;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring", uses = {PatientMapper.class, UserMapper.class})
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

