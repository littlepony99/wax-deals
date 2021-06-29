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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
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
    public String getCatalogPage(@SessionAttribute(value = "user", required = false) User user,
                                 @RequestParam(value = "wantlist", required = false) String wantList,
                                 Model model) {
        uniqueVinylService.prepareCatalog(user, model, wantList);
        return "catalog";
    }

}
