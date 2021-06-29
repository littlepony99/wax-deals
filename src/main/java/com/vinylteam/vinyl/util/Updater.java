package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.ShopsParser;
import com.vinylteam.vinyl.util.impl.VinylParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class Updater {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopsParser shopsParser;
    private final List<VinylParser> vinylParsers;
    private final RawOffersSorter sorter;

    public void updateUniqueVinylsRewriteOffers() {
        List<UniqueVinyl> uniqueVinyls = uniqueVinylService.findAll();
        List<RawOffer> rawOffers = shopsParser.getRawOffersFromAll(vinylParsers);
        List<Offer> newOffers = sorter.getOffersUpdateUniqueVinyls(rawOffers, uniqueVinyls);
        offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, newOffers);
    }

}
