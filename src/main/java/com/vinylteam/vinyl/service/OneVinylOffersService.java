package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;

import java.util.HashMap;
import java.util.List;

public interface OneVinylOffersService {

    OneVinylPageDto prepareOneVinylInfo(String id, Long userId) throws NotFoundException;

    HashMap<String, List> getSortedInStockOffersAndShops(String identifier) throws NotFoundException;

    List<UniqueVinyl> addAuthorVinyls(UniqueVinyl uniqueVinyl);

    String getDiscogsLink(UniqueVinyl uniqueVinyl);

}
