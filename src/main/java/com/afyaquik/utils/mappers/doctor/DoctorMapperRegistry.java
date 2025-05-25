package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.utils.mappers.MapperRegistry;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapperRegistry {
    DoctorMapperRegistry(ObservationItemMapper observationItemMapper, MapperRegistry mapperRegistry)
    {
        {
            mapperRegistry.registerMapper("observationItems", observationItemMapper);
        }
    }
}
