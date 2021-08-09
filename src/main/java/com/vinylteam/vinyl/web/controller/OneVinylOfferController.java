package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.impl.DefaultOneVinylOffersService;
import com.vinylteam.vinyl.util.impl.OneVinylOfferMapper;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oneVinyl")
@CrossOrigin(origins = {"http://localhost:3000", "https://react-wax-deals.herokuapp.com"})
public class OneVinylOfferController {

    private final DefaultOneVinylOffersService oneVinylOffersService;

    private final UniqueVinylMapper uniqueVinylMapper;
    private final OneVinylOfferMapper oneVinylOfferMapper;

    @GetMapping("/{id}")
    public OneVinylPageDto getOneVinylOfferPage(@SessionAttribute(value = "user", required = false) User user,
                                                @PathVariable("id") String id) {
        UniqueVinyl uniqueVinyl = oneVinylOffersService.getUniqueVinyl(id);
        HashMap<String, List> offersAndShopsMap = oneVinylOffersService.getSortedInStockOffersAndShops(id);
        List<Shop> shops = offersAndShopsMap.get("shops");
        List<Offer> offers = offersAndShopsMap.get("offers");
        List<OneVinylOfferDto> offerDtoList = offers.stream()
                .map(offer -> oneVinylOfferMapper.offerAndShopToVinylOfferDto(offer, oneVinylOffersService.findOfferShop(shops, offer)))
                .collect(Collectors.toList());
        List<UniqueVinyl> vinyls = oneVinylOffersService.addAuthorVinyls(uniqueVinyl);
        vinyls.remove(uniqueVinyl);
        String discogsLink = oneVinylOffersService.getDiscogsLink(uniqueVinyl);
        return OneVinylPageDto.builder()
                .discogsLink(discogsLink)
                .mainVinyl(uniqueVinylMapper.uniqueVinylToDto(uniqueVinyl))
                .offersList(offerDtoList)
                .vinylsByArtistList(uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(vinyls))
                .build();
    }

}
