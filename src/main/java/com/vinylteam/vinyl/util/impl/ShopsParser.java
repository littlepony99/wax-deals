package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.impl.VinylParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ShopsParser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<RawOffer> getRawOffersFromAll(List<VinylParser> vinylParserList) {
        List<RawOffer> allRawOffers = new ArrayList<>();
        for (VinylParser vinylParser : vinylParserList) {
            allRawOffers.addAll(vinylParser.getRawOffersList());
            logger.debug("Added all vinyls from vinyl parser to list of all vinyls {'vinylParser':{}}", vinylParser);
            logger.info("got all raw offers from {}", vinylParser.getClass());
        }
        logger.debug("Resulting list of all vinyls from all shops is {'allRawOffers':{}}", allRawOffers);
        return allRawOffers;
    }

}
