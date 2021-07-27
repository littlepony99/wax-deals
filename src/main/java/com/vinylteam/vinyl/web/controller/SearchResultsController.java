package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/search")
@CrossOrigin(origins = { "http://localhost:3000", "http://react-wax-deals.herokuapp.com" })
public class SearchResultsController {

    private final UniqueVinylService vinylService;

    @GetMapping
    public String getSearchResultPage(@SessionAttribute(value = "user", required = false) User user,
                                      @RequestParam(value = "matcher") String matcher,
                                      Model model) {
        List<UniqueVinyl> filteredUniqueVinyls = vinylService.findByFilter(matcher);
        model.addAttribute("matcher", matcher);
        WebUtils.setUserAttributes(user, model);
        WebUtils.setModelContext(filteredUniqueVinyls, new ArrayList<>(), model);
        return "search";
    }

}
