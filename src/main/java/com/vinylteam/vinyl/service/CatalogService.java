package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;

import java.util.List;
import java.util.Map;

public interface CatalogService {

    List<UniqueVinylDto> findRandomUniqueVinyls(int amount);

    OneVinylPageDto getOneVinylPageDto(String id, Long userId) throws NotFoundException;

    Map<String, List<?>> getSortedInStockOffersAndShops(String identifier) throws NotFoundException;

    List<UniqueVinyl> getOtherUniqueVinylsByVinylArtist(UniqueVinyl uniqueVinyl);

    String getDiscogsLink(UniqueVinyl uniqueVinyl);

}
