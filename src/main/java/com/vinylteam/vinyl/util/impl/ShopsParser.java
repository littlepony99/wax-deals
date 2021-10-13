package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ShopsParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<RawOffer> getRawOffersFromAll(List<VinylParser> vinylParserList) {
        List<RawOffer> allRawOffers = new ArrayList<>();
        for (VinylParser vinylParser : vinylParserList) {
            log.info("Starting parser for shop by id {'shopId':{}}", vinylParser.getShopId());
            allRawOffers.addAll(vinylParser.getRawOffersList());
            log.debug("Added all vinyls from vinyl parser to list of all vinyls {'vinylParser':{}}", vinylParser);
            log.info("got all raw offers from parser for shop by id {'shopId':{}}", vinylParser.getShopId());
        }
        log.debug("Resulting list of all vinyls from all shops is {'allRawOffers':{}}", allRawOffers);
        return allRawOffers;
    }

}
