package com.vinylteam.vinyl.web.util;

import com.vinylteam.vinyl.entity.*;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class WebUtils {

    public static void setUserAttributes(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("userRole", user.getRole().toString());
            }
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

    public static void setModelContext(List<UniqueVinyl> vinylList, List<OneVinylOffersServletResponse> vinylOffersList, Model model) {
        List<UniqueVinyl> firstUniqueVinylRow = new ArrayList<>();
        List<UniqueVinyl> otherUniqueVinylRow = new ArrayList<>();
        List<UniqueVinyl> uniqueVinylsByArtist = new ArrayList<>();

// for catalog page

        model.addAttribute("vinylList", vinylList);

// for search & one vinyl with offers pages

        if (!vinylList.isEmpty()) {
            model.addAttribute("firstVinyl", vinylList.get(0));
        }

// for search page

        if (vinylList.size() > 1) {
            if (vinylList.size() >= 7) {
                for (int i = 1; i < 7; i++) {
                    firstUniqueVinylRow.add(vinylList.get(i));
                }
            } else {
                for (int i = 1; i < vinylList.size(); i++) {
                    firstUniqueVinylRow.add(vinylList.get(i));
                }
            }
            model.addAttribute("firstVinylRow", firstUniqueVinylRow);
        }
        if (vinylList.size() > 7) {
            for (int i = 7; i < vinylList.size(); i++) {
                otherUniqueVinylRow.add(vinylList.get(i));
            }
            model.addAttribute("otherVinylRow", otherUniqueVinylRow);
        }

// for one vinyl with offers page

        if (vinylList.size() > 1) {
            for (int i = 1; i < vinylList.size(); i++) {
                uniqueVinylsByArtist.add(vinylList.get(i));
            }
            model.addAttribute("vinylsByArtist", uniqueVinylsByArtist);
        }

        model.addAttribute("vinylOffersList", vinylOffersList);
    }

}
