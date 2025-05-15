package com.afyaquik.core.mappers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class MapperRegistry {
    private final Map<String, EntityMapper<?, ?>> mapperMap = new HashMap<>();

    @Autowired
    public MapperRegistry(
            RoleMapper roleMapper,
            UserMapper userMapper,
            StationMapper stationMapper
            // add more mappers
    ) {
        mapperMap.put("roles", roleMapper);
        mapperMap.put("users", userMapper);
        mapperMap.put("stations", stationMapper);
    }
    public void registerMapper(String entityName, EntityMapper<?, ?> mapper) {
        mapperMap.put(entityName, mapper);
    }

    public EntityMapper<Object, Object> getMapper(String entityName) {
        return (EntityMapper<Object, Object>) mapperMap.get(entityName);
    }
}
