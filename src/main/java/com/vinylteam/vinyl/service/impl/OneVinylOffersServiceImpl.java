package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.*;
import com.vinylteam.vinyl.util.impl.ParserHolder;
import com.vinylteam.vinyl.util.impl.VinylParser;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import com.vinylteam.vinyl.web.dto.OneVinylPageFullResponse;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OneVinylOffersServiceImpl implements OneVinylOffersService {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;
    private final DiscogsService discogsService;
    private final ParserHolder parserHolder;

    @Override
    public OneVinylPageFullResponse prepareOneVinylInfo(String identifier) {
        long uniqueVinylId = Long.parseLong(identifier);
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(uniqueVinylId);

        List<Offer> offers = offerService.findManyByUniqueVinylId(uniqueVinyl.getId());
        List<Integer> shopIds = offerService.getListOfShopIds(offers);
        List<Shop> shopsFromOffers = shopService.getManyByListOfIds(shopIds);

        List<OneVinylOffersServletResponse> offersResponseList = prepareOffersSection(offers, shopsFromOffers);

        checkIsVinylInStock(uniqueVinyl, offersResponseList);

        List<UniqueVinyl> preparedVinylsList = prepareVinylsSection(uniqueVinyl);

        String discogsLink = getDiscogsLink(uniqueVinyl);

        return new OneVinylPageFullResponse(offersResponseList, preparedVinylsList, discogsLink);
    }

    void checkIsVinylInStock(UniqueVinyl uniqueVinyl, List<OneVinylOffersServletResponse> offersResponseList) {
        if (offersResponseList.isEmpty()) {
            uniqueVinyl.setHasOffers(false);
            uniqueVinylService.updateOneUniqueVinylAsHavingNoOffer(uniqueVinyl);
        }
    }

    String getDiscogsLink(UniqueVinyl uniqueVinyl) {
        try {
            return discogsService.getDiscogsLink(uniqueVinyl.getArtist(),
                    uniqueVinyl.getRelease(), uniqueVinyl.getFullName());
        } catch (ParseException e) {
            log.error("Error while getting discogs link for unique vinyl, ParseException thrown {'uniqueVinyl':{}}", uniqueVinyl, e);
            return "";
        }
    }

    List<OneVinylOffersServletResponse> prepareOffersSection(List<Offer> dbOffers, List<Shop> shopsFromOffers) {
        List<OneVinylOffersServletResponse> offersResponseList = new ArrayList<>();

        for (Offer dbOffer : dbOffers) {
            var actualOffer = getActualOffer(dbOffer);

            if (actualOffer.isInStock()) {
                var shop = findShop(shopsFromOffers, actualOffer);
                OneVinylOffersServletResponse offersResponse = WebUtils.getOfferResponseFromOffer(dbOffer, shop);
                offersResponseList.add(offersResponse);
            }
        }
        offersResponseList.sort((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()));
        return offersResponseList;
    }

    Offer getActualOffer(Offer dbOffer) {
        var shopParser = parserHolder
                .getShopParserByShopId(dbOffer.getShopId());
        var actualOffer = shopParser
                .stream()
                .map(parser -> parser.getRawOfferFromOfferLink(dbOffer.getOfferLink()))
                .findFirst()
                .orElse(new RawOffer());
        offerService.mergeOfferChanges(dbOffer, shopParser.get(), actualOffer);
        return dbOffer;
    }

    Shop findShop(List<Shop> shopsFromOffers, Offer offer) {
        return shopsFromOffers
                .stream()
                .filter(store -> Objects.equals(store.getId(), offer.getShopId()))
                .findFirst()
                .get();
    }

    List<UniqueVinyl> prepareVinylsSection(UniqueVinyl uniqueVinyl) {
        List<UniqueVinyl> preparedListById = new ArrayList<>(List.of(uniqueVinyl));

        String artist = uniqueVinyl.getArtist();
        uniqueVinylService.findManyByArtist(artist)
                .stream()
                .filter(v -> v.getId() != uniqueVinyl.getId())
                .forEach(v -> preparedListById.add(v));

        return preparedListById;
    }

}