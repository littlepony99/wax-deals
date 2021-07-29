package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;

import java.util.List;

public interface OneVinylOffersService {
    List<OneVinylOfferDto> getOffers(String identifier);

    List<UniqueVinyl> addAuthorVinyls(UniqueVinyl uniqueVinyl);

    String getDiscogsLink(UniqueVinyl uniqueVinyl);

}
