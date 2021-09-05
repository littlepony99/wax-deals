package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.util.impl.ShopMapper;
import com.vinylteam.vinyl.web.dto.ShopDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/stores")
public class ShopController {

    private final ShopService shopService;
    private final ShopMapper shopMapper;

    @GetMapping
    public List<ShopDto> getShopInfo() {
        List<ShopDto> result = shopService
                .findAll()
                .stream()
                .map(shopMapper::userToUserDto)
                .collect(toList());
        log.info("Shops list is prepared to be included in response, size {'shopsListSize':{}}", result.size());
        return result;
    }

}
