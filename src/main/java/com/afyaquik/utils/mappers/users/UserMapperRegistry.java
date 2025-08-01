package com.afyaquik.utils.mappers.users;

import com.afyaquik.utils.mappers.MapperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapperRegistry {
    @Autowired
    UserMapperRegistry(RoleMapper roleMapper, UserMapper userMapper, StationMapper stationMapper, ApiPermissionMapper apiPermissionMapper, MapperRegistry mapperRegistry)
    {
        {
            mapperRegistry.registerMapper("stations", stationMapper);
            mapperRegistry.registerMapper("users", userMapper);
            mapperRegistry.registerMapper("roles", roleMapper);
            mapperRegistry.registerMapper("apiPermissions", apiPermissionMapper);
        }
    }
}
