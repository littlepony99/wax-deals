package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.impl.OneVinylOffersServiceImpl;
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

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/oneVinyl")
public class OneVinylOfferController {

    private final OneVinylOffersServiceImpl oneVinylOffersService;

    @GetMapping
    public String getOneVinylOfferPage(@SessionAttribute(value = "user", required = false) User user,
                                       @RequestParam(value = "id") String id,
                                       Model model) {
        WebUtils.setUserAttributes(user, model);
        OneVinylPageFullResponse fullResponse = oneVinylOffersService.prepareOneVinylInfo(id);

        SetDiscogsAttribute(model, fullResponse);

        WebUtils.setModelContext(fullResponse, model);
        return "vinyl";
    }

    void SetDiscogsAttribute(Model model, OneVinylPageFullResponse fullResponse) {
        String discogsLink = fullResponse.getDiscogsLink();

        if (!discogsLink.isEmpty()) {
            model.addAttribute("discogsLink", discogsLink);
        }
    }

}
