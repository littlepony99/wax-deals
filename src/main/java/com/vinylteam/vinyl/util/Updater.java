package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.UniqueVinylService;

import java.util.List;

public class Updater {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopsParser shopsParser;
    private final List<VinylParser> vinylParsers;
    private final RawOffersSorter sorter;

    public Updater(UniqueVinylService uniqueVinylService, OfferService offerService, ShopsParser shopsParser, List<VinylParser> vinylParsers, RawOffersSorter sorter) {
        this.uniqueVinylService = uniqueVinylService;
        this.offerService = offerService;
        this.shopsParser = shopsParser;
        this.vinylParsers = vinylParsers;
        this.sorter = sorter;
    }

    public void updateUniqueVinylsRewriteOffers() {
        List<UniqueVinyl> uniqueVinyls = uniqueVinylService.findAll();
        List<RawOffer> rawOffers = shopsParser.getRawOffersFromAll(vinylParsers);
        List<Offer> newOffers = sorter.getOffersUpdateUniqueVinyls(rawOffers, uniqueVinyls);
        offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, newOffers);
    }

}
