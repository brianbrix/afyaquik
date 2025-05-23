package com.afyaquik.utils.mappers;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class MapperRegistry {
    private final Map<String, EntityMapper<?, ?>> mapperMap = new HashMap<>();

    public void registerMapper(String entityName, EntityMapper<?, ?> mapper) {
        mapperMap.put(entityName, mapper);
    }

    public EntityMapper<Object, Object> getMapper(String entityName) {
        return (EntityMapper<Object, Object>) mapperMap.get(entityName);
    }
}
