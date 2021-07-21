package com.vinylteam.vinyl.web.util;

import com.vinylteam.vinyl.entity.*;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import com.vinylteam.vinyl.web.dto.OneVinylPageFullResponse;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

public class WebUtils {

    public static void setUserAttributes(User user, Model model) {
        if (user != null) {
            model.addAttribute("userRole", user.getRole().getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("discogsUserName", user.getDiscogsUserName());
        }
    }

    public static OneVinylOffersServletResponse getOfferResponseFromOffer(Offer offer, Shop shop) {
        var offersResponse = new OneVinylOffersServletResponse();

        offersResponse.setPrice(offer.getPrice());

        String offerCurrency = offer.getCurrency().map(Currency::getSymbol).orElse("");
        offersResponse.setCurrency(offerCurrency);

        offersResponse.setCatNumber(offer.getCatNumber());
        offersResponse.setInStock(offer.isInStock());
        offersResponse.setOfferLink(offer.getOfferLink());
        offersResponse.setShopImageLink(shop.getSmallImageLink());
        return offersResponse;
    }

    public static void setModelContext(OneVinylPageFullResponse fullResponse, Model model) {

        setModelContext(fullResponse.getPreparedVinyls(), fullResponse.getOffersResponses(), model);
    }

    public static void setModelContext(List<UniqueVinyl> vinyls, List<OneVinylOffersServletResponse> vinylOffers, Model model) {
        List<UniqueVinyl> firstUniqueVinylRow = new ArrayList<>();
        List<UniqueVinyl> otherUniqueVinylRow = new ArrayList<>();
        List<UniqueVinyl> uniqueVinylsByArtist = new ArrayList<>();

// for catalog page

        model.addAttribute("vinyls", vinyls);

// for search & one vinyl with offers pages

        if (!vinyls.isEmpty()) {
            model.addAttribute("firstVinyl", vinyls.get(0));
        }

// for search page

        if (vinyls.size() > 1) {
            if (vinyls.size() >= 7) {
                for (int i = 1; i < 7; i++) {
                    firstUniqueVinylRow.add(vinyls.get(i));
                }
            } else {
                for (int i = 1; i < vinyls.size(); i++) {
                    firstUniqueVinylRow.add(vinyls.get(i));
                }
            }
            model.addAttribute("firstVinylRow", firstUniqueVinylRow);
        }
        if (vinyls.size() > 7) {
            for (int i = 7; i < vinyls.size(); i++) {
                otherUniqueVinylRow.add(vinyls.get(i));
            }
            model.addAttribute("otherVinylRow", otherUniqueVinylRow);
        }

// for one vinyl with offers page

        if (vinyls.size() > 1) {
            for (int i = 1; i < vinyls.size(); i++) {
                uniqueVinylsByArtist.add(vinyls.get(i));
            }
            model.addAttribute("vinylsByArtist", uniqueVinylsByArtist);
        }

        model.addAttribute("vinylOffersList", vinylOffers);
    }

}
