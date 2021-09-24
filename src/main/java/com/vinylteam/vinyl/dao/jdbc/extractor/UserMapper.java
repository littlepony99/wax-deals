package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.UserDto;
import org.mapstruct.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, implementationName = "DefaultUserMapper")
public interface UserMapper {

    @Mappings({@Mapping(source = "email", target = "username"),
            @Mapping(source = "role", target = "authorities"),
            @Mapping(source = "status", target = "enabled")
    })
    JwtUser mapToDto(User user);

    UserDto mapUserToDto(User user);

    default List<SimpleGrantedAuthority> roleToSimpleGrantedAuthoritiesList(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

}
