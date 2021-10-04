package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.service.*;
import com.vinylteam.vinyl.util.impl.OneVinylOfferMapper;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOneVinylOffersService implements OneVinylOffersService {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;
    private final DiscogsService discogsService;
    private final WantListService wantListService;

    private final UniqueVinylMapper uniqueVinylMapper;
    private final OneVinylOfferMapper oneVinylOfferMapper;

    public UniqueVinyl getUniqueVinyl(String id) throws NotFoundException {
        return uniqueVinylService.findById(id);
    }

    @Override
    public OneVinylPageDto prepareOneVinylInfo(String id, Long userId) throws NotFoundException {
        UniqueVinyl uniqueVinyl = getUniqueVinyl(id);
        HashMap<String, List> offersAndShopsMap = getSortedInStockOffersAndShops(id);
        List<Shop> shops = offersAndShopsMap.get("shops");
        List<Offer> offers = offersAndShopsMap.get("offers");
        List<OneVinylOfferDto> offerDtoList = offers.stream()
                .map(offer -> oneVinylOfferMapper.offerAndShopToVinylOfferDto(offer, findOfferShop(shops, offer)))
                .collect(Collectors.toList());
        List<UniqueVinyl> vinyls = addAuthorVinyls(uniqueVinyl);
        vinyls.remove(uniqueVinyl);
        String discogsLink = getDiscogsLink(uniqueVinyl);
        List<UniqueVinylDto> artistVinyls = uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(vinyls);
        List<UniqueVinylDto> mergedArtistVinyls = wantListService.mergeVinylsWithWantList(userId, artistVinyls);
        UniqueVinylDto mainVinyl = uniqueVinylMapper.uniqueVinylToDto(uniqueVinyl);
        List<UniqueVinylDto> mergedMainVinyl = wantListService.mergeVinylsWithWantList(userId, List.of(mainVinyl));
        return OneVinylPageDto.builder()
                .discogsLink(discogsLink)
                .mainVinyl(mergedMainVinyl.get(0))
                .offersList(offerDtoList)
                .vinylsByArtistList(mergedArtistVinyls)
                .build();
    }

    @Override
    public HashMap<String, List> getSortedInStockOffersAndShops(String identifier) throws NotFoundException {
        String uniqueVinylId = identifier;
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(uniqueVinylId);
        List<Offer> offers = offerService.findByUniqueVinylId(uniqueVinyl.getId());
        List<Offer> sortedInStockOffers = getInStockOffersByPrice(offers);
        List<Integer> shopIds = offerService.findShopIds(sortedInStockOffers);
        List<Shop> shopsFromOffers = shopService.findShopsByListOfIds(shopIds);
        checkIsVinylInStock(uniqueVinyl, sortedInStockOffers);

        HashMap<String, List> offersAndShopsMap = new HashMap<>();
        offersAndShopsMap.put("offers", sortedInStockOffers);
        offersAndShopsMap.put("shops", shopsFromOffers);

        return offersAndShopsMap;
    }

    @Override
    public List<UniqueVinyl> addAuthorVinyls(UniqueVinyl uniqueVinyl) {
        return prepareVinylsSection(uniqueVinyl);
    }

    @Override
    public String getDiscogsLink(UniqueVinyl uniqueVinyl) {
        try {
            return discogsService.getDiscogsLink(uniqueVinyl.getArtist(),
                    uniqueVinyl.getRelease(), uniqueVinyl.getFullName());
        } catch (ParseException e) {
            log.error("Error while getting discogs link for unique vinyl, ParseException thrown {'uniqueVinyl':{}}", uniqueVinyl, e);
            return "";
        }
    }

    void checkIsVinylInStock(UniqueVinyl uniqueVinyl, List<Offer> inStockOffers) {
        if (inStockOffers.isEmpty()) {
            uniqueVinyl.setHasOffers(false);
            uniqueVinylService.updateOneUniqueVinyl(uniqueVinyl);
        }
    }

    List<Offer> getInStockOffersByPrice(List<Offer> dbOffers) {
        return dbOffers.stream()
                .map(offerService::actualizeOffer)
                .filter(Offer::isInStock)
                .sorted((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()))
                .collect(Collectors.toList());
    }

    Shop findOfferShop(List<Shop> shopsList, Offer offer) {
        return shopsList
                .stream()
                .filter(store -> store.getId() == offer.getShopId())
                .findFirst()
                .get();
    }

    List<UniqueVinyl> prepareVinylsSection(UniqueVinyl uniqueVinyl) {
        List<UniqueVinyl> preparedListById = new ArrayList<>();

        uniqueVinylService.findByArtist(uniqueVinyl.getArtist())
                .stream()
                .filter(v -> v.getId() != uniqueVinyl.getId())
                .forEach(preparedListById::add);

        return preparedListById;
    }

}