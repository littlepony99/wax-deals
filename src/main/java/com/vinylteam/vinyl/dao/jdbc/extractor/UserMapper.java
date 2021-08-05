package com.vinylteam.vinyl.dao.jdbc.extractor;


import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.UserDto;
import org.mapstruct.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Mapper(collectionMappingStrategy= CollectionMappingStrategy.SETTER_PREFERRED, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, implementationName = "DefaultUserMapper")
public interface UserMapper {

    @Mappings({@Mapping(source = "email", target = "username"),
            @Mapping(source = "role", target = "authorities")})
    JwtUser mapToDto(User user);

    UserDto mapUserToDto(User user);

    default List<SimpleGrantedAuthority> roleToSimpleGrantedAuthoritiesList(Role role){
        ArrayList<SimpleGrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
        return list;
    }
}
