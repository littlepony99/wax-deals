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
@CrossOrigin(origins = { "http://localhost:3000", "http://react-wax-deals.herokuapp.com" })
public class OneVinylOfferController {

    private final OneVinylOffersServiceImpl oneVinylOffersService;
    private final UniqueVinylMapper uniqueVinylMapper;

    @GetMapping("/{id}")
    public OneVinylPageDto getOneVinylOfferPage(@SessionAttribute(value = "user", required = false) User user,
                                                @PathVariable("id") String id) {
        UniqueVinyl uniqueVinyl = oneVinylOffersService.getUniqueVinyl(id);
        List<OneVinylOfferDto> offers = oneVinylOffersService.getOffers(id);
        List<UniqueVinyl> vinyls = oneVinylOffersService.addAuthorVinyls(uniqueVinyl);
        String discogsLink = oneVinylOffersService.getDiscogsLink(uniqueVinyl);
        OneVinylPageDto result = OneVinylPageDto.builder()
                .discogsLink(discogsLink)
                .mainVinyl(uniqueVinylMapper.uniqueVinylToDto(uniqueVinyl))
                .offersList(offers)
                .vinylsByArtistList(uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(vinyls))
                .build();
        return result;
    }

}
