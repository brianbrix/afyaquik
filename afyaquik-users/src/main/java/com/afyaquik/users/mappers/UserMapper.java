package com.afyaquik.users.mappers;

import com.afyaquik.core.mappers.EntityMapper;
import com.afyaquik.dtos.user.UserDto;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<User, UserDto> {
    @Override
    UserDto toDto(User user);

    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
    default Set<String> mapStations(Set<Station> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Station::getName)
                .collect(Collectors.toSet());
    }
}
