package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/search")
public class SearchResultsController {

    private final UniqueVinylService vinylService;

    @GetMapping
    public String getSearchResultPage(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Model model) {
        String matcher = request.getParameter("matcher");
        List<UniqueVinyl> filteredUniqueVinyls = vinylService.findManyFiltered(matcher);
        model.addAttribute("matcher", matcher);
        WebUtils.setUserAttributes(request, model);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        WebUtils.setModelContext(filteredUniqueVinyls, new ArrayList<>(), model);
        return "search";
    }
}
