package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.OfferRepository;
import com.vinylteam.vinyl.dao.elasticsearch.UniqueVinylRepository;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.util.impl.ParserHolder;
import com.vinylteam.vinyl.util.impl.VinylParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultOfferService implements OfferService {
    private final OfferRepository offerRepository;
    private final UniqueVinylRepository uniqueVinylRepository;
    private final ParserHolder parserHolder;

    @Override
    public List<Offer> findByUniqueVinylId(String uniqueVinylId) {
        if (uniqueVinylId == null) {
            log.error("uniqueVinylId is null");
            throw new IllegalArgumentException("uniqueVinylId is null");
        }
        List<Offer> offers = offerRepository.findByUniqueVinylId(uniqueVinylId);
        log.info("Resulting list of vinyls fetched {'size':{}}", offers.size());
        log.debug("Resulting list of vinyls is {'vinyls':{}}", offers);
        return offers;
    }

    @Override
    public void updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {
        if (CollectionUtils.isEmpty(uniqueVinyls)) {
            log.error("List of unique vinyls is null or empty");
            throw new IllegalArgumentException("List of unique vinyls is null or empty");
        }
        if (CollectionUtils.isEmpty(offers)) {
            log.error("List of offers is null or empty");
            throw new IllegalArgumentException("List of offers is null or empty");
        }
        save(uniqueVinyls, offers);
        log.info("Successfully updated database with {} unique vinyls and {} offers", uniqueVinyls.size(), offers.size());
    }

    public void save(List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {
        uniqueVinylRepository.saveAll(uniqueVinyls);
        offerRepository.deleteAll();
        offerRepository.saveAll(offers);
    }

    public Offer actualizeOffer(Offer dbOffer) {
        return parserHolder
                .getShopParserByShopId(dbOffer.getShopId())
                .map(parser -> mergeOfferChanges(dbOffer, parser, parser.getRawOfferFromOfferLink(dbOffer.getOfferLink())))
                .orElse(dbOffer);
    }

    @Override
    public List<Integer> findShopIds(List<Offer> offers) {
        if (offers == null) {
            return new ArrayList<>();
        }
        List<Integer> shopsIds = offers.stream()
                .map(Offer::getShopId)
                .distinct()
                .collect(Collectors.toList());
        log.debug("Resulting list of shop id-s is {'shopIds':{}}", shopsIds);
        return shopsIds;
    }

    @Override
    public Offer mergeOfferChanges(Offer offer, VinylParser shopParser, RawOffer dynamicOffer) {
        if (shopParser.isValid(dynamicOffer)) {
            var actualPrice = dynamicOffer.getPrice();
            var actualCurrency = dynamicOffer.getCurrency();
            offer.setInStock(dynamicOffer.isInStock());
            offer.setCurrency(actualCurrency);
            offer.setPrice(actualPrice);
        } else {
            offer.setInStock(false);
        }
        return offer;
    }

}
