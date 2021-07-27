package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/catalog")
@CrossOrigin(origins = { "http://localhost:3000", "http://react-wax-deals.herokuapp.com" })
public class CatalogController {

    private final UniqueVinylService uniqueVinylService;

    @GetMapping
    public String getCatalogPage(@SessionAttribute(value = "user", required = false) User user,
                                 @RequestParam(value = "wantlist", required = false) String wantList,
                                 Model model) {
        uniqueVinylService.prepareCatalog(user, model, wantList);
        return "catalog";
    }

}
