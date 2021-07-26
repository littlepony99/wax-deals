package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.web.controller.ShopController;
import com.vinylteam.vinyl.web.dto.ShopDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ShopController.class)
public interface ShopMapper {

    ShopDto userToUserDto(Shop shop);

}