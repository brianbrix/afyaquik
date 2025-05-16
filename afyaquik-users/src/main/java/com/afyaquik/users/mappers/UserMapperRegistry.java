package com.afyaquik.users.mappers;

import com.afyaquik.core.mappers.MapperRegistry;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMapperRegistry {
    @Autowired
    UserMapperRegistry(RoleMapper roleMapper, UserMapper userMapper, StationMapper stationMapper, MapperRegistry mapperRegistry)
    {
        {
            mapperRegistry.registerMapper("stations", stationMapper);
            mapperRegistry.registerMapper("users", userMapper);
            mapperRegistry.registerMapper("roles", roleMapper);
        }
    }
}
