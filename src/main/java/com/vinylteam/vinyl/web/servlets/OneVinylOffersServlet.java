package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.ParserHolder;
import com.vinylteam.vinyl.util.impl.VinylParser;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
public class OneVinylOffersServlet extends HttpServlet {

    private final UniqueVinylService uniqueVinylService;
    private final OfferService offerService;
    private final ShopService shopService;
    private final DiscogsService discogsService;

    private final ParserHolder parserHolder;

    public OneVinylOffersServlet(UniqueVinylService uniqueVinylService, OfferService offerService,
                                 ShopService shopService, DiscogsService discogsService, ParserHolder parserHolder) {
        this.uniqueVinylService = uniqueVinylService;
        this.offerService = offerService;
        this.shopService = shopService;
        this.discogsService = discogsService;
        this.parserHolder = parserHolder;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        List<OneVinylOffersServletResponse> offersResponseList = new ArrayList<>();
//        Map<String, String> attributes = new HashMap<>();
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            User user = (User) session.getAttribute("user");
//            if (user != null) {
//                attributes.put("userRole", user.getRole().toString());
//            }
//        }
//
//        long uniqueVinylId = Long.parseLong(request.getParameter("id"));
//        UniqueVinyl uniqueVinyl = uniqueVinylService.findById(uniqueVinylId);
//
//        List<Offer> offers = offerService.findManyByUniqueVinylId(uniqueVinyl.getId());
//        List<Integer> shopIds = offerService.getListOfShopIds(offers);
//        List<Shop> shopsFromOffers = shopService.getManyByListOfIds(shopIds);
//
//        try {
//            String discogsLink = discogsService.getDiscogsLink(uniqueVinyl.getArtist(),
//                    uniqueVinyl.getRelease(), uniqueVinyl.getFullName());
//            attributes.put("discogsLink", discogsLink);
//        } catch (ParseException e) {
//            log.error("Error while getting discogs link for unique vinyl, ParseException thrown {'uniqueVinyl':{}}", uniqueVinyl, e);
//        }
//
//        for (Offer offer : offers) {
//            var offerShopParser = parserHolder.getShopParserByShopId(offer.getShopId());
//            var shop = shopsFromOffers.stream().filter(store -> store.getId() == offer.getShopId()).findFirst().get();
//            if (offerShopParser.isPresent()) {
//                VinylParser shopParser = offerShopParser.get();
//
//                var dynamicOffer = shopParser.getRawOfferFromOfferLink(offer.getOfferLink());
//                offerService.mergeOfferChanges(offer, shopParser, dynamicOffer);
//
//                if (offer.isInStock()) {
//                    OneVinylOffersServletResponse offersResponse = getOfferResponseFromOffer(offer, shop);
//                    offersResponseList.add(offersResponse);
//                }
//            }
//        }
//
//        List<UniqueVinyl> preparedListById = new ArrayList<>();
//        preparedListById.add(0, uniqueVinyl);
//
//        response.setContentType("text/html;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        String artist = uniqueVinyl.getArtist();
//        List<UniqueVinyl> uniqueVinylsByArtist = uniqueVinylService.findManyByArtist(artist);
//
//        if (!uniqueVinylsByArtist.isEmpty()) {
//            for (UniqueVinyl uniqueVinylByArtist : uniqueVinylsByArtist) {
//                if (uniqueVinyl.getId() != uniqueVinylByArtist.getId()) {
//                    preparedListById.add(uniqueVinylByArtist);
//                }
//            }
//        }
//
//        if (offersResponseList.isEmpty()) {
//            uniqueVinyl.setHasOffers(false);
//            uniqueVinylService.updateOneUniqueVinylAsHavingNoOffer(uniqueVinyl);
//            attributes.put("message", "No any offer found at the moment for the selected vinyl. Try to find it later");
//            PageGenerator.getInstance().process("vinyl", preparedListById, offersResponseList, attributes, response.getWriter());
//            return;
//        }
//
//        offersResponseList.sort((offer1, offer2) -> (int) (offer1.getPrice() - offer2.getPrice()));
//
//        PageGenerator.getInstance().process("vinyl", preparedListById, offersResponseList, attributes, response.getWriter());
//    }
//
//    OneVinylOffersServletResponse getOfferResponseFromOffer(Offer offer, Shop shop) {
//        var offersResponse = new OneVinylOffersServletResponse();
//
//        offersResponse.setPrice(offer.getPrice());
//
//        String offerCurrency = offer.getCurrency().map(currency -> currency.getSymbol()).orElse("");
//        offersResponse.setCurrency(offerCurrency);
//
//        offersResponse.setCatNumber(offer.getCatNumber());
//        offersResponse.setInStock(offer.isInStock());
//        offersResponse.setOfferLink(offer.getOfferLink());
//        offersResponse.setShopImageLink(shop.getSmallImageLink());
//        return offersResponse;
    }

}
