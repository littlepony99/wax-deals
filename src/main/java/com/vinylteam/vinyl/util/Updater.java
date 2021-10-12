package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.ShopsParser;
import com.vinylteam.vinyl.util.impl.VinylParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class Updater {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopsParser shopsParser;
    private final List<VinylParser> vinylParsers;
    private final RawOffersSorter sorter;

    public void updateUniqueVinylsRewriteOffers() {
        List<UniqueVinyl> uniqueVinyls = uniqueVinylService.findAll();
        log.info("Found all unique vinyls from db {'uniqueVinylsSize':{}}", uniqueVinyls.size());
        List<RawOffer> rawOffers = shopsParser.getRawOffersFromAll(vinylParsers);
        List<Offer> newOffers = sorter.getOffersUpdateUniqueVinyls(rawOffers, uniqueVinyls);
        log.info("Sorted raw offers into unique vinyls and new offers {'uniqueVinylsSize':{}, 'offersSize':{}}", uniqueVinyls.size(), newOffers.size());
        offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, newOffers);
    }

}
