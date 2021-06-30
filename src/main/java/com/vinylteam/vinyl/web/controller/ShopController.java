package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/stores")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public String getShopPage(@SessionAttribute(value = "user", required = false) User user,
                              Model model) {
        var shopList = shopService.findAll();
        log.info("Shops list is prepared to be included in response, size {'shopsListSize':{}}", shopList.size());
        WebUtils.setUserAttributes(user, model);
        model.addAttribute("shopList", shopList);
        return "stores";
    }

}
