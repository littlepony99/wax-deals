package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.util.impl.VinylParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultOfferService implements OfferService {

    @Override
    public List<Offer> findManyByUniqueVinylId(long uniqueVinylId) {
        return null;
    }

    @Override
    public void updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {

    }

    @Override
    public List<Integer> getListOfShopIds(List<Offer> offers) {
        return null;
    }

    @Override
    public void mergeOfferChanges(Offer offer, VinylParser shopParser, RawOffer dynamicOffer) {
    }

}
