package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.impl.OneVinylOffersServiceImpl;
import com.vinylteam.vinyl.util.impl.ParserHolder;
import com.vinylteam.vinyl.web.dto.OneVinylPageFullResponse;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/oneVinyl")
public class OneVinylOfferController {

    private final OneVinylOffersServiceImpl oneVinylOffersService;

    @GetMapping
    public String getOneVinylOfferPage(@SessionAttribute(value = "user", required = false) User user,
                                       @RequestParam(value = "id") String identifier,
                                       Model model) {
        WebUtils.setUserAttributes(user, model);
        OneVinylPageFullResponse fullResponse = oneVinylOffersService.prepareOneVinylInfo(identifier);

        //Discogs section
        SetDiscogsAttribute(model, fullResponse);
        setMessageAttribute(model, fullResponse);

        WebUtils.setModelContext(fullResponse, model);
        return "vinyl";
    }

    private void setMessageAttribute(Model model, OneVinylPageFullResponse fullResponse) {
        if (fullResponse.getOffersResponseList().isEmpty()) {
            model.addAttribute("message", "No any offer found at the moment for the selected vinyl. Try to find it later");
        }
    }

    void SetDiscogsAttribute(Model model, OneVinylPageFullResponse fullResponse) {
        String discogsLink = fullResponse.getDiscogsLink();

        if (!discogsLink.isEmpty()) {
            model.addAttribute("discogsLink", discogsLink);
        }
    }

}
