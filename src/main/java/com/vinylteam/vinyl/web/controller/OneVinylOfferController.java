package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.impl.OneVinylOffersServiceImpl;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oneVinyl")
public class OneVinylOfferController {

    private final OneVinylOffersServiceImpl oneVinylOffersService;
    private final UniqueVinylMapper uniqueVinylMapper;

    @GetMapping
    public OneVinylPageDto getOneVinylOfferPage(@SessionAttribute(value = "user", required = false) User user,
                                                @RequestParam(value = "id") String identifier) {
        UniqueVinyl uniqueVinyl = oneVinylOffersService.getUniqueVinyl(identifier);
        List<OneVinylOfferDto> offers = oneVinylOffersService.getOffers(identifier);
        List<UniqueVinyl> vinyls = oneVinylOffersService.addAuthorVinyls(uniqueVinyl);
        String discogsLink = oneVinylOffersService.getDiscogsLink(uniqueVinyl);
        OneVinylPageDto result = OneVinylPageDto.builder()
                .discogsLink(discogsLink)
                .offersResponseList(offers)
                .preparedVinylsList(uniqueVinylMapper.listVinylsToListVinylsDto(vinyls))
                .build();
        return result;
    }

}
