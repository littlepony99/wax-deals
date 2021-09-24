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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultCatalogService implements CatalogService {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;
    private final DiscogsService discogsService;
    private final WantListService wantListService;
    private final OneVinylOfferMapper offerMapper;
    private final UniqueVinylMapper uniqueVinylMapper;

    public UniqueVinyl getUniqueVinyl(String id) throws NotFoundException {
        return uniqueVinylService.findById(id);
    }

    @Override
    public List<UniqueVinylDto> findRandomUniqueVinyls(int amount) {
        return uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(uniqueVinylService.findRandom(amount));
    }

    @Override
    public OneVinylPageDto getOneVinylPageDto(String id, Long userId) throws NotFoundException {
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(id);
        Map<String, List<?>> offersAndShopsMap = getSortedInStockOffersAndShops(id);
        List<Shop> shops = (List<Shop>) offersAndShopsMap.get("shops");
        List<Offer> offers = (List<Offer>) offersAndShopsMap.get("offers");
        List<OneVinylOfferDto> offerDtoList = offers.stream()
                .map(offer -> offerMapper.offerAndShopToVinylOfferDto(offer, findOfferShop(shops, offer)))
                .collect(Collectors.toList());
        List<UniqueVinyl> vinyls = getOtherUniqueVinylsByVinylArtist(uniqueVinyl);
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
    public Map<String, List<?>> getSortedInStockOffersAndShops(String identifier) throws NotFoundException {
        // find vinyl
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(identifier);
        // find offers for uniqueVinyl
        List<Offer> offers = offerService.findByUniqueVinylId(uniqueVinyl.getId());
        // 1 prepare offers -- validate and parse
        List<Offer> sortedInStockOffers = getActualizedInStockOffersByPrice(offers);
        // find shopIds for offers
        List<Integer> shopIds = offerService.findShopIds(sortedInStockOffers);
        // find shops by their ids
        List<Shop> shopsFromOffers = shopService.findShopsByListOfIds(shopIds);
        // if there is no offers
        checkIsVinylInStock(uniqueVinyl, sortedInStockOffers);

        Map<String, List<?>> offersAndShopsMap = new HashMap<>();
        offersAndShopsMap.put("offers", sortedInStockOffers);
        offersAndShopsMap.put("shops", shopsFromOffers);

        return offersAndShopsMap;
    }

    @Override
    public List<UniqueVinyl> getOtherUniqueVinylsByVinylArtist(UniqueVinyl uniqueVinyl) {
        List<UniqueVinyl> preparedListById = new ArrayList<>();
        uniqueVinylService.findByArtist(uniqueVinyl.getArtist())
                .stream()
                .filter(v -> v.getId() != uniqueVinyl.getId())
                .forEach(preparedListById::add);
        return preparedListById;
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

    List<Offer> getActualizedInStockOffersByPrice(List<Offer> dbOffers) {
        return dbOffers.stream()
                .map(offerService::actualizeOffer)
                .filter(Offer::isInStock)
                .sorted((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()))
                .collect(Collectors.toList());
    }

    public Shop findOfferShop(List<Shop> shopsList, Offer offer) {
        return shopsList
                .stream()
                .filter(store -> store.getId() == offer.getShopId())
                .findFirst()
                .get();
    }

}