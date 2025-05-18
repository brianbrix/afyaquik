package com.afyaquik.appointments.mappers;

import com.afyaquik.core.mappers.MapperRegistry;
import com.afyaquik.users.mappers.StationMapper;
import com.afyaquik.users.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapperRegistry {
    @Autowired
    AppointmentMapperRegistry(AppointmentMapper appointmentMapper, MapperRegistry mapperRegistry)
    {
        {

            mapperRegistry.registerMapper("appointments", appointmentMapper);
        }
    }
}
