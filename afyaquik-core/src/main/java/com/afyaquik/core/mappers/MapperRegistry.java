package com.afyaquik.core.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MapperRegistry {
    private final Map<String, EntityMapper<?, ?>> mapperMap = new HashMap<>();

    @Autowired
    public MapperRegistry(
            RoleMapper roleMapper,
            PatientMapper patientMapper,
            PatientVisitMapper patientVisitMapper,
            UserMapper userMapper,
            StationMapper stationMapper
            // add more mappers
    ) {
        mapperMap.put("roles", roleMapper);
        mapperMap.put("patients", patientMapper);
        mapperMap.put("visits", patientVisitMapper);
        mapperMap.put("users", userMapper);
        mapperMap.put("stations", stationMapper);
    }

    public EntityMapper<Object, Object> getMapper(String entityName) {
        return (EntityMapper<Object, Object>) mapperMap.get(entityName);
    }
}
