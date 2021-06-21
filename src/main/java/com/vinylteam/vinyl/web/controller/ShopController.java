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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/stores")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public String getShopPage(HttpSession session,
                              HttpServletResponse response,
                              Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        var shopList = shopService.findAll();
        log.info("Shops list is prepared to be included in response, size {'shopsListSize':{}}", shopList.size());
        WebUtils.setUserAttributes(session, model);
        model.addAttribute("shopList", shopList);
        return "stores";
    }
}
