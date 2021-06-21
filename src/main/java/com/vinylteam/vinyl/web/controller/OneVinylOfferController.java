package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.ParserHolder;
import com.vinylteam.vinyl.util.impl.VinylParser;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/oneVinyl")
public class OneVinylOfferController {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;
    private final DiscogsService discogsService;
    private final ParserHolder parserHolder;

    @GetMapping
    public String getOneVinylOfferPage(HttpSession session,
                                       @RequestParam(value = "id") String stringId,
                                       HttpServletResponse response,
                                       Model model) {
        List<OneVinylOffersServletResponse> offersResponseList = new ArrayList<>();
        WebUtils.setUserAttributes(session, model);

        long uniqueVinylId = Long.parseLong(stringId);
        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(uniqueVinylId);

        List<Offer> offers = offerService.findManyByUniqueVinylId(uniqueVinyl.getId());
        List<Integer> shopIds = offerService.getListOfShopIds(offers);
        List<Shop> shopsFromOffers = shopService.getManyByListOfIds(shopIds);

        try {
            String discogsLink = discogsService.getDiscogsLink(uniqueVinyl.getArtist(),
                    uniqueVinyl.getRelease(), uniqueVinyl.getFullName());
            if (!discogsLink.isEmpty()) {
                model.addAttribute("discogsLink", discogsLink);
            }
        } catch (ParseException e) {
            log.error("Error while getting discogs link for unique vinyl, ParseException thrown {'uniqueVinyl':{}}", uniqueVinyl, e);
        }

        for (Offer offer : offers) {
            var offerShopParser = parserHolder.getShopParserByShopId(offer.getShopId());
            var shop = shopsFromOffers.stream()
                    .filter(store -> store.getId() == offer.getShopId())
                    .findFirst().get();
            if (offerShopParser.isPresent()) {
                VinylParser shopParser = offerShopParser.get();

                var dynamicOffer = shopParser.getRawOfferFromOfferLink(offer.getOfferLink());
                offerService.mergeOfferChanges(offer, shopParser, dynamicOffer);

                if (offer.isInStock()) {
                    OneVinylOffersServletResponse offersResponse = WebUtils.getOfferResponseFromOffer(offer, shop);
                    offersResponseList.add(offersResponse);
                }
            }
        }

        List<UniqueVinyl> preparedListById = new ArrayList<>();
        preparedListById.add(0, uniqueVinyl);

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String artist = uniqueVinyl.getArtist();
        List<UniqueVinyl> uniqueVinylsByArtist = uniqueVinylService.findManyByArtist(artist);

        if (!uniqueVinylsByArtist.isEmpty()) {
            for (UniqueVinyl uniqueVinylByArtist : uniqueVinylsByArtist) {
                if (uniqueVinyl.getId() != uniqueVinylByArtist.getId()) {
                    preparedListById.add(uniqueVinylByArtist);
                }
            }
        }

        if (offersResponseList.isEmpty()) {
            uniqueVinyl.setHasOffers(false);
            uniqueVinylService.updateOneUniqueVinylAsHavingNoOffer(uniqueVinyl);
            model.addAttribute("message", "No any offer found at the moment for the selected vinyl. Try to find it later");
            WebUtils.setModelContext(preparedListById, offersResponseList, model);
            return "vinyl";
        }

        offersResponseList.sort((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()));

        WebUtils.setModelContext(preparedListById, offersResponseList, model);
        return "vinyl";
    }
}
