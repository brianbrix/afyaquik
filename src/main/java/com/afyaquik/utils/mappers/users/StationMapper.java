package com.afyaquik.utils.mappers.users;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.users.dto.StationDto;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StationMapper extends EntityMapper<Station, StationDto> {

    @Override
    @Mapping(target = "allowedRoles", source = "allowedRoles", qualifiedByName = "mapRolesToStrings")
    StationDto toDto(Station station);

    @Mapping(target = "allowedRoles", ignore = true)
    Station toEntity(StationDto dto);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
