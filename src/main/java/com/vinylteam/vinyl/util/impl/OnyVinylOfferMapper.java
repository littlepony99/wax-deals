package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.web.controller.ShopController;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = ShopController.class)
public interface OnyVinylOfferMapper {
    @Mappings({
            @Mapping(target = "shopImageLink", constant = "shop.smallImageLink"),
            @Mapping(target = "currency", source = "offer.currency", qualifiedByName = "currency")
    })
    OneVinylOfferDto offerAndShopToVinylOfferDto(Offer offer, Shop shop);

    @Named("currency")
    default <T> T unwrap(Optional<T> currency) {
        return currency.orElse(null);
    }
}
