package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ShopService;
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

    private ShopService shopService;

    @GetMapping
    public String getShopPage(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        var shopList = shopService.findAll();
        log.info("Shops list is prepared to be included in response, size {'shopsListSize':{}}", shopList.size());
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("userRole", user.getRole().toString());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("discogsUserName", user.getDiscogsUserName());
            }
        }
        model.addAttribute("shopList", shopList);
        return "stores";
    }
}
