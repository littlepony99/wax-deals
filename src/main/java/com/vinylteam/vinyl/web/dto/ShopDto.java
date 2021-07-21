package com.vinylteam.vinyl.web.dto;

import com.vinylteam.vinyl.entity.Shop;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopDto {
    private int id;
    private String mainPageLink;
    private String imageLink;
    private String smallImageLink;
    private String name;

    public static ShopDto toDto(Shop shop) {
        return ShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .smallImageLink(shop.getSmallImageLink())
                .mainPageLink(shop.getMainPageLink())
                .imageLink(shop.getImageLink())
                .build();
    }
}
