package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.web.dto.ShopDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/stores")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public List<ShopDto> getShopPage() {
        var shopList = shopService.findAll();
        List<ShopDto> result = new ArrayList<>();
        if (null != shopList && !shopList.isEmpty()) {
            for (Shop shop : shopList) {
                result.add(ShopDto.toDto(shop));
            }
        }
        log.info("Shops list is prepared to be included in response, size {'shopsListSize':{}}", result.size());
        return result;
    }

}
