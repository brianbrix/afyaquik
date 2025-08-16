package com.afyaquik.utils.mappers.users;


import com.afyaquik.users.dto.RoleResponse;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<Role, RoleResponse> {

    @Override
    @Mapping(target = "stations", ignore = true)
    RoleResponse toDto(Role entity);

    @Named("mapStationsToStrings")
    static Set<String> mapStationsToStrings(Set<Station> stations) {
        if (stations == null) {
            return null;
        }
        return stations.stream()
                .map(Station::getName)
                .collect(Collectors.toSet());
    }
}

