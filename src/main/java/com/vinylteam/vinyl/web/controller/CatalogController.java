package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/catalog")
public class CatalogController {

    private final DiscogsService discogsService;
    private final UniqueVinylService uniqueVinylService;

    @GetMapping
    public String getCatalogPage(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Model model) {
        String discogsUserName;
        User user = null;
        List<UniqueVinyl> randomUniqueVinyls = uniqueVinylService.findManyRandom(50);
        List<UniqueVinyl> forShowing = new ArrayList<>();
        List<UniqueVinyl> allUniqueVinyl = uniqueVinylService.findAll();
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session = request.getSession(false);
        String isWantListEmpty = request.getParameter("wantlist");
        if (session != null && isWantListEmpty == null) {
            user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("userRole", user.getRole().toString());
                discogsUserName = user.getDiscogsUserName();
                forShowing = discogsService.getDiscogsMatchList(discogsUserName, allUniqueVinyl);
            }
        }
        if (user != null) {
            model.addAttribute("vinylList", forShowing);
        } else {
            model.addAttribute("vinylList", randomUniqueVinyls);
        }
        return "catalog";
    }
}
