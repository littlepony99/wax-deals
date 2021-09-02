package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;

import java.util.HashMap;
import java.util.List;

public interface OneVinylOffersService {

    OneVinylPageDto prepareOneVinylInfo(String id);

    HashMap<String, List> getSortedInStockOffersAndShops(String identifier);

    List<UniqueVinyl> addAuthorVinyls(UniqueVinyl uniqueVinyl);

    String getDiscogsLink(UniqueVinyl uniqueVinyl);

}
