package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.web.dto.ShopDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopMapper {
    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    ShopDto userToUserDto(Shop shop);

}