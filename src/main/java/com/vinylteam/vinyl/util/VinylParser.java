package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.RawOffer;

import java.util.List;

public interface VinylParser {

    List<RawOffer> getRawOffersList();

    RawOffer getRawOfferFromOfferLink(String offerLink);

}
