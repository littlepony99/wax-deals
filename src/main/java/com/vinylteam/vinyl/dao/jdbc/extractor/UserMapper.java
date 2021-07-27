package com.vinylteam.vinyl.dao.jdbc.extractor;


import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, implementationName = "DefaultUserMapper")
public interface UserMapper {

    @Mappings({@Mapping(source = "email", target = "username")})
    JwtUser mapToDto(User user);
}
